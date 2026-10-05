package project.camus.observability.mashup

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestClient
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MashupResilienceTest extends Specification {

    static final String MEMBER_PATH = "/camus/v1/members/1"

    static final String TASK_PATH = "/camus/v1/tasks"

    static WireMockServer downstream = new WireMockServer(wireMockConfig().dynamicPort())

    static {
        downstream.start()
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("feign-url.member", { "${downstream.baseUrl()}/camus/v1/members" })
        registry.add("feign-url.task", { "${downstream.baseUrl()}/camus/v1/tasks" })
        registry.add("spring.cloud.openfeign.client.config.default.read-timeout", { 500 })
        registry.add("resilience4j.circuitbreaker.configs.default.wait-duration-in-open-state", { "1s" })
        registry.add("resilience4j.retry.configs.default.wait-duration", { "10ms" })
    }

    @Value('${local.server.port}')
    int port

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry

    RestClient client

    def setup() {

        downstream.resetAll()
        circuitBreakerRegistry.allCircuitBreakers.each { it.reset() }
        client = RestClient.builder()
            .baseUrl("http://localhost:$port/camus/v1/member")
            .defaultStatusHandler({ true }, { request, response -> })
            .build()
    }

    def cleanupSpec() {

        downstream.stop()
    }

    def "returns member and tasks when downstream services are healthy"() {

        given:
        memberOk()
        tasksOk()

        when:
        def response = findMemberTasks()

        then:
        response.statusCode.value() == 200
        with(response.body.result) {
            member.name == "camus"
            tasks.size() == 2
            degraded == false
        }
    }

    def "returns partial response after retries when task service fails"() {

        given:
        memberOk()
        downstream.stubFor(get(urlPathEqualTo(TASK_PATH)).willReturn(aResponse().withStatus(503)))

        when:
        def response = findMemberTasks()

        then:
        response.statusCode.value() == 200
        response.body.result.member.name == "camus"
        response.body.result.tasks == []
        response.body.result.degraded == true
        taskCalls() == 3
    }

    def "slow task service times out and returns partial response"() {

        given:
        memberOk()
        downstream.stubFor(get(urlPathEqualTo(TASK_PATH)).willReturn(okJson(tasksJson()).withFixedDelay(1_000)))

        when:
        def response = findMemberTasks()

        then: "timeout is retried only by resilience4j (feign retryer is disabled)"
        response.statusCode.value() == 200
        response.body.result.degraded == true
        taskCalls() == 3
    }

    def "member service failure is retried and returns 503"() {

        given:
        downstream.stubFor(get(urlPathEqualTo(MEMBER_PATH)).willReturn(aResponse().withStatus(500)))
        tasksOk()

        when:
        def response = findMemberTasks()

        then:
        response.statusCode.value() == 503
        response.body.errors == ["downstream service is unavailable"]
        memberCalls() == 3
    }

    def "4xx from member service is not retried"() {

        given:
        downstream.stubFor(get(urlPathEqualTo(MEMBER_PATH)).willReturn(aResponse().withStatus(404)))

        when:
        def response = findMemberTasks()

        then:
        response.statusCode.value() == 400
        memberCalls() == 1
        circuitBreaker("member").metrics.numberOfFailedCalls == 0
    }

    def "circuit opens after repeated failures and stops calling task service"() {

        given:
        memberOk()
        downstream.stubFor(get(urlPathEqualTo(TASK_PATH)).willReturn(aResponse().withStatus(503)))

        when: "2 requests x 3 attempts = 6 failed calls (minimum 5, 100% failure)"
        findMemberTasks()
        findMemberTasks()

        then:
        circuitBreaker("task").state == CircuitBreaker.State.OPEN

        when:
        downstream.resetRequests()
        def response = findMemberTasks()

        then: "task service is not called while the circuit is open"
        response.body.result.degraded == true
        taskCalls() == 0
    }

    def "circuit closes again when task service recovers"() {

        given:
        memberOk()
        downstream.stubFor(get(urlPathEqualTo(TASK_PATH)).willReturn(aResponse().withStatus(503)))
        findMemberTasks()
        findMemberTasks()
        assert circuitBreaker("task").state == CircuitBreaker.State.OPEN

        when: "task service recovers and the open duration passes"
        tasksOk()
        new PollingConditions(timeout: 5).eventually {
            assert circuitBreaker("task").state == CircuitBreaker.State.HALF_OPEN
        }
        def responses = (1..2).collect { findMemberTasks() }

        then:
        responses*.body*.result*.degraded == [false, false]
        circuitBreaker("task").state == CircuitBreaker.State.CLOSED
    }

    private ResponseEntity<Map> findMemberTasks() {

        client.get().uri("/{memberId}/tasks", 1).retrieve().toEntity(Map)
    }

    private CircuitBreaker circuitBreaker(String name) {

        circuitBreakerRegistry.circuitBreaker(name)
    }

    private void memberOk() {

        downstream.stubFor(get(urlPathEqualTo(MEMBER_PATH)).willReturn(okJson(
            '{"timestamp":1,"result":{"id":1,"name":"camus","age":10,"address":"Seoul"}}')))
    }

    private void tasksOk() {

        downstream.stubFor(get(urlPathEqualTo(TASK_PATH)).willReturn(okJson(tasksJson())))
    }

    private static String tasksJson() {

        '{"timestamp":1,"result":[{"id":1,"memberId":1,"description":"a","priority":"HIGH"},' +
            '{"id":2,"memberId":1,"description":"b","priority":"LOW"}]}'
    }

    private static okJson(String body) {

        aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(body)
    }

    private int memberCalls() {

        downstream.countRequestsMatching(getRequestedFor(urlPathEqualTo(MEMBER_PATH)).build()).count
    }

    private int taskCalls() {

        downstream.countRequestsMatching(getRequestedFor(urlPathEqualTo(TASK_PATH)).build()).count
    }
}
