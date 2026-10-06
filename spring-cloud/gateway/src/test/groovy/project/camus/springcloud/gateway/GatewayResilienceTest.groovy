package project.camus.springcloud.gateway

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.any
import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestClient
import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.GenericContainer
import project.camus.test.support.TestJwtIssuer
import spock.lang.Requires
import spock.lang.Specification

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [
    "spring.cloud.bootstrap.enabled=false",
    "spring.cloud.config.enabled=false",
    "spring.cloud.bus.enabled=false",
    "eureka.client.enabled=false",
    // 테스트에는 rabbitmq(cloud bus) 가 없다.
    "management.health.rabbit.enabled=false",
])
class GatewayResilienceTest extends Specification {

    static final String TASKS_PATH = "/v1/tasks"

    static WireMockServer taskService = new WireMockServer(wireMockConfig().dynamicPort())

    // auth-server 대신 JWKS 를 제공하고 토큰을 서명한다.
    static TestJwtIssuer issuer = new TestJwtIssuer()

    static GenericContainer redis = new GenericContainer("redis:7.4-alpine").withExposedPorts(6379)

    static {
        taskService.start()
        if (DockerClientFactory.instance().isDockerAvailable()) {
            redis.start()
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("TASK_SERVICE_URI", { taskService.baseUrl() })
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", { TestJwtIssuer.ISSUER })
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", { "${taskService.baseUrl()}/oauth2/jwks" })
        registry.add("spring.data.redis.host", { redis.host })
        registry.add("spring.data.redis.port", { redis.getMappedPort(6379) })
        registry.add("GATEWAY_RATE_LIMIT_REPLENISH_RATE", { 1 })
        registry.add("GATEWAY_RATE_LIMIT_BURST_CAPACITY", { 5 })
        registry.add("spring.cloud.gateway.server.webflux.httpclient.response-timeout", { "500ms" })
        registry.add("resilience4j.timelimiter.configs.default.timeout-duration", { "3s" })
    }

    @Value('${local.server.port}')
    int port

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry

    RestClient client

    // rate limit 버킷(사용자별)이 테스트끼리 섞이지 않도록 테스트마다 다른 사용자 토큰을 쓴다.
    String user

    def setup() {

        taskService.resetAll()
        taskService.stubFor(get(urlPathEqualTo("/oauth2/jwks")).willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(issuer.jwksJson())))
        circuitBreakerRegistry.allCircuitBreakers.each { it.reset() }
        user = "user-${UUID.randomUUID()}"
        client = newClient()
    }

    // apache httpclient 5 는 503/429 를 기본으로 한 번 재시도해서 gateway 동작을 가리므로, 재시도하지 않는 JDK HttpClient 를 쓴다.
    private RestClient newClient(String token = issuer.token(user, ["task.read", "task.write"])) {

        def builder = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .baseUrl("http://localhost:$port/task-service")
            .defaultStatusHandler({ true }, { request, response -> })
        if (token != null) {
            builder.defaultHeader("Authorization", "Bearer $token")
        }
        builder.build()
    }

    def cleanupSpec() {

        taskService.stop()
    }

    def "routes to task service with trace id and rate limit headers"() {

        given:
        okTasks()

        when:
        def response = request(HttpMethod.GET)

        then:
        response.statusCode.value() == 200
        response.headers.getFirst("Trace-Id") != null
        response.headers.getFirst("X-RateLimit-Remaining") != null
        taskCalls() == 1
    }

    def "trace id from the client is kept in Trace-Id header and propagated downstream"() {

        given:
        def traceId = "5b8aa5a2d2c872e8321cf37308d69df2"
        okTasks()

        when:
        def response = client.get().uri(TASKS_PATH)
            .header("traceparent", "00-$traceId-051581bf3cb55c13-01")
            .retrieve().toEntity(Map)
        def downstreamTraceParent = taskService.allServeEvents.first().request.getHeader("traceparent")

        then:
        response.headers.getFirst("Trace-Id") == traceId
        downstreamTraceParent.startsWith("00-$traceId-")
    }

    def "GET is retried on 503 and falls back to 503 json"() {

        given:
        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse().withStatus(503)))

        when:
        def response = request(HttpMethod.GET)

        then: "1 attempt + 2 retries"
        response.statusCode.value() == 503
        response.body.errors == ["task-service is unavailable"]
        taskCalls() == 3
    }

    def "POST is not retried"() {

        given:
        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse().withStatus(503)))

        when:
        def response = request(HttpMethod.POST)

        then:
        response.statusCode.value() == 503
        taskCalls() == 1
    }

    def "slow response times out and falls back"() {

        given:
        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse().withStatus(200).withFixedDelay(2_000)))

        when:
        def response = request(HttpMethod.GET)

        then:
        response.statusCode.value() == 503
        response.body.errors == ["task-service is unavailable"]
    }

    def "circuit opens after repeated failures and stops calling task service"() {

        given:
        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse().withStatus(500)))

        when: "5 failed requests (minimum number of calls)"
        5.times { request(HttpMethod.POST) }

        then:
        circuitBreaker().state == CircuitBreaker.State.OPEN

        when: "다른 사용자로 요청 (앞의 5건으로 rate limit 버킷을 다 썼으므로)"
        taskService.resetRequests()
        user = "other-${UUID.randomUUID()}"
        def response = newClient().post().uri(TASKS_PATH).retrieve().toEntity(Map)

        then:
        response.statusCode.value() == 503
        taskCalls() == 0
    }

    def "requests over the burst capacity are rejected with 429"() {

        given:
        okTasks()

        when:
        def statuses = (1..10).collect { request(HttpMethod.GET).statusCode.value() }

        then: "burst capacity 5"
        statuses.count { it == 200 } <= 6
        statuses.count { it == 429 } >= 4

        when: "another user is not affected"
        user = "other-${UUID.randomUUID()}"
        def other = newClient().get().uri(TASKS_PATH).retrieve().toEntity(Map)

        then:
        other.statusCode.value() == 200
    }

    def "gateway retries do not consume client rate limit tokens"() {

        given:
        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse().withStatus(503)))

        when: "a failed GET is retried twice inside the gateway"
        request(HttpMethod.GET)
        okTasks()
        def next = request(HttpMethod.GET)

        then: "burst 5 - 2 requests = 3, 재시도가 토큰을 쓰면 최대 2 (초당 1개 보충 고려)"
        next.statusCode.value() == 200
        (next.headers.getFirst("X-RateLimit-Remaining") as int) >= 3
    }

    def "requests without a valid token are rejected with 401 and not routed"() {

        given:
        okTasks()

        expect:
        newClient(token).get().uri(TASKS_PATH).retrieve().toEntity(Map).statusCode.value() == 401
        taskCalls() == 0

        where:
        token << [null, "not-a-jwt", new TestJwtIssuer("other-key").token("camus", ["task.read"]),
                  issuer.expiredToken("camus", ["task.read"])]
    }

    def "write requires task.write scope"() {

        given:
        okTasks()
        def readOnly = newClient(issuer.token(user, ["task.read"]))

        expect:
        readOnly.get().uri(TASKS_PATH).retrieve().toEntity(Map).statusCode.value() == 200
        readOnly.post().uri(TASKS_PATH).retrieve().toEntity(Map).statusCode.value() == 403
        taskCalls() == 1
    }

    def "the validated token is relayed to the downstream service"() {

        given:
        okTasks()
        def token = issuer.token(user, ["task.read"])

        when:
        newClient(token).get().uri(TASKS_PATH).retrieve().toEntity(Map)

        then:
        taskService.allServeEvents.find { it.request.url.startsWith(TASKS_PATH) }.request.getHeader("Authorization") ==
            "Bearer $token"
    }

    def "unauthenticated requests do not consume the rate limit"() {

        given:
        okTasks()

        when: "burst 5 를 넘는 인증 실패 요청"
        def statuses = (1..10).collect { newClient(null).get().uri(TASKS_PATH).retrieve().toEntity(Map).statusCode.value() }

        then: "모두 401 이고 429 는 없다"
        statuses.every { it == 401 }
    }

    def "downstream actuator is not reachable through the gateway even with a valid token"() {

        given:
        taskService.stubFor(any(urlPathEqualTo("/actuator/env")).willReturn(aResponse().withStatus(200)))

        when:
        def response = client.get().uri("/actuator/env").retrieve().toEntity(Map)

        then:
        response.statusCode.value() == 403
        taskService.countRequestsMatching(anyRequestedFor(urlPathEqualTo("/actuator/env")).build()).count == 0
    }

    def "gateway exposes only health and prometheus"() {

        given:
        def gateway = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .baseUrl("http://localhost:$port")
            .defaultStatusHandler({ true }, { request, response -> })
            .build()

        expect:
        gateway.get().uri(path).retrieve().toBodilessEntity().statusCode.value() == status

        where:
        path                    | status
        "/actuator/health"      | 200
        "/actuator/prometheus"  | 200
        "/actuator/beans"       | 404
        "/actuator/env"         | 404
        "/actuator/busrefresh"  | 404
    }

    private ResponseEntity<Map> request(HttpMethod method) {

        client.method(method).uri(TASKS_PATH).retrieve().toEntity(Map)
    }

    private CircuitBreaker circuitBreaker() {

        circuitBreakerRegistry.circuitBreaker("task-service")
    }

    private void okTasks() {

        taskService.stubFor(any(urlPathEqualTo(TASKS_PATH)).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody('{"timestamp":1,"result":{"tasks":[]}}')))
    }

    private int taskCalls() {

        taskService.countRequestsMatching(anyRequestedFor(urlPathEqualTo(TASKS_PATH)).build()).count
    }
}
