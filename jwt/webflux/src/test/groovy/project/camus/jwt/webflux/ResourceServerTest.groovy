package project.camus.jwt.webflux

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.client.RestClient
import project.camus.test.support.TestJwtIssuer
import spock.lang.Specification

/**
 * auth-server 대신 테스트 발급기의 JWKS 를 쓰는 resource server 검증.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResourceServerTest extends Specification {

    static TestJwtIssuer issuer = new TestJwtIssuer()

    static WireMockServer authServer = new WireMockServer(wireMockConfig().dynamicPort())

    static {
        authServer.start()
        authServer.stubFor(get(urlPathEqualTo("/oauth2/jwks")).willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(issuer.jwksJson())))
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", { TestJwtIssuer.ISSUER })
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", { "${authServer.baseUrl()}/oauth2/jwks" })
    }

    @Value('${local.server.port}')
    int port

    def cleanupSpec() {

        authServer.stop()
    }

    def "returns the user and authorities from a valid token"() {

        when:
        def response = me(issuer.token("camus", ["task.read"], ["USER"]))

        then:
        response.statusCode.value() == 200
        response.body.result.username == "camus"
        // spring security 7 은 인증 방식을 나타내는 FACTOR_BEARER 권한도 함께 넣는다.
        response.body.result.authorities.containsAll(["ROLE_USER", "SCOPE_task.read", "FACTOR_BEARER"])
    }

    def "rejects requests without a valid token with 401"() {

        expect:
        me(token).statusCode.value() == 401

        where:
        token << [
            null,
            "not-a-jwt",
            new TestJwtIssuer("other-key").token("camus", ["task.read"]),   // 다른 키로 서명
            issuer.expiredToken("camus", ["task.read"]),                    // 만료
            issuer.tokenWithIssuer("camus", ["task.read"], "http://evil"),  // 다른 발급자
        ]
    }

    private ResponseEntity<Map> me(String token) {

        def request = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .baseUrl("http://localhost:$port")
            .defaultStatusHandler({ true }, { req, res -> })
            .build()
            .get().uri("/users/me")
        if (token != null) {
            request.header("Authorization", "Bearer $token")
        }
        request.retrieve().toEntity(Map)
    }
}
