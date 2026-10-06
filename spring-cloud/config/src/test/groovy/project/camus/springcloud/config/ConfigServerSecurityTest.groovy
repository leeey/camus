package project.camus.springcloud.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import spock.lang.Specification

/**
 * git 저장소와 KMS 없이 native(classpath) 저장소로 띄워 인증/인가만 검증한다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = [
    "spring.profiles.active=native",
    "spring.cloud.config.server.native.search-locations=classpath:/test-config-repo/",
    "camus.config.git.enabled=false",
    "camus.config.encryption.enabled=false",
    "spring.cloud.bus.enabled=false",
    "management.health.rabbit.enabled=false",
])
class ConfigServerSecurityTest extends Specification {

    @Value('${local.server.port}')
    int port

    def "config is served only to authenticated clients"() {

        expect:
        call(HttpMethod.GET, "/gateway/default", null) == 401
        call(HttpMethod.GET, "/gateway/default", "config-client:wrong") == 401
        call(HttpMethod.GET, "/gateway/default", "config-client:config-client-secret") == 200
    }

    def "client can read the config"() {

        when:
        def body = client("config-client:config-client-secret").get().uri("/gateway/default").retrieve().body(Map)

        then:
        body.propertySources*.source*.get("camus.message").contains("hello from config server")
    }

    def "encrypt, decrypt and busrefresh are for admins only"() {

        expect:
        call(HttpMethod.POST, path, null) == 401
        call(HttpMethod.POST, path, "config-client:config-client-secret") == 403
        !(call(HttpMethod.POST, path, "config-admin:config-admin-secret") in [401, 403])

        where:
        path << ["/encrypt", "/decrypt", "/actuator/busrefresh"]
    }

    def "health is open"() {

        expect:
        call(HttpMethod.GET, "/actuator/health", null) == 200
    }

    private int call(HttpMethod method, String path, String credentials) {

        client(credentials).method(method).uri(path).body("text").retrieve().toBodilessEntity().statusCode.value()
    }

    private RestClient client(String credentials) {

        def builder = RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory())
            .baseUrl("http://localhost:$port")
            .defaultStatusHandler({ true }, { request, response -> })
        if (credentials != null) {
            builder.defaultHeader("Authorization", "Basic ${credentials.bytes.encodeBase64()}")
        }
        builder.build()
    }
}
