package project.camus.authserver

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.DockerClientFactory
import org.testcontainers.postgresql.PostgreSQLContainer
import spock.lang.Requires
import spock.lang.Specification
import tools.jackson.databind.json.JsonMapper

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthServerIntegrationTest extends Specification {

    static final String REDIRECT_URI = "http://127.0.0.1:8080/login/oauth2/code/camus-web"

    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine")

    static {
        if (DockerClientFactory.instance().isDockerAvailable()) {
            postgres.start()
        }
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", { postgres.jdbcUrl })
        registry.add("spring.datasource.username", { postgres.username })
        registry.add("spring.datasource.password", { postgres.password })
    }

    @Value('${local.server.port}')
    int port

    @Autowired
    MockMvc mockMvc

    @Autowired
    JdbcTemplate jdbcTemplate

    def json = JsonMapper.builder().build()

    def "publishes oidc discovery and the public signing key (jwks)"() {

        given:
        def client = RestClient.create("http://localhost:$port")

        when:
        def discovery = client.get().uri("/.well-known/openid-configuration").retrieve().body(Map)
        def jwks = client.get().uri("/oauth2/jwks").retrieve().body(Map)

        then:
        discovery.issuer == "http://localhost:9400"
        discovery.jwks_uri == "http://localhost:9400/oauth2/jwks"
        discovery.grant_types_supported.containsAll(["authorization_code", "client_credentials", "refresh_token"])
        jwks.keys.size() == 1
        with(jwks.keys.first()) {
            kty == "RSA"
            kid == "camus-local"
            d == null // 공개키만 공개한다
        }
    }

    def "issues a RS256 access token with client credentials"() {

        when:
        def response = mockMvc.perform(post("/oauth2/token")
            .with(httpBasic("camus-service", "camus-service-secret"))
            .param("grant_type", "client_credentials")
            .param("scope", "task.read"))
            .andReturn().response
        def body = json.readValue(response.contentAsString, Map)
        def jwt = decoder().decode(body.access_token as String)

        then:
        response.status == 200
        jwt.headers.alg as String == "RS256"
        jwt.headers.kid == "camus-local"
        jwt.subject == "camus-service"
        jwt.getClaimAsStringList("scope") == ["task.read"]
        jdbcTemplate.queryForObject(
            "SELECT count(*) FROM oauth2_authorization WHERE registered_client_id = 'camus-service'", Integer) >= 1
    }

    def "rejects a wrong client secret"() {

        expect:
        mockMvc.perform(post("/oauth2/token")
            .with(httpBasic("camus-service", "wrong"))
            .param("grant_type", "client_credentials"))
            .andReturn().response.status == 401
    }

    def "authorization code with PKCE issues user tokens, and refresh tokens rotate"() {

        given:
        def verifier = codeVerifier()
        def session = login("camus", "camus")

        when: "로그인한 사용자가 authorize 하면 code 가 발급된다"
        def code = authorize(session, verifier)
        def tokens = token([grant_type   : "authorization_code", code: code, redirect_uri: REDIRECT_URI,
                            code_verifier: verifier])
        def accessToken = decoder().decode(tokens.access_token as String)

        then:
        accessToken.subject == "camus"
        accessToken.getClaimAsStringList("roles") == ["USER"]
        accessToken.getClaimAsStringList("scope").containsAll(["task.read", "task.write"])
        tokens.refresh_token != null
        tokens.id_token != null

        when: "refresh token 을 쓰면 새 refresh token 으로 바뀐다"
        def refreshed = token([grant_type: "refresh_token", refresh_token: tokens.refresh_token])

        then:
        refreshed.access_token != null
        refreshed.refresh_token != tokens.refresh_token

        when: "이미 쓴 refresh token 은 다시 쓸 수 없다"
        def reused = mockMvc.perform(post("/oauth2/token")
            .with(httpBasic("camus-web", "camus-web-secret"))
            .param("grant_type", "refresh_token")
            .param("refresh_token", tokens.refresh_token as String))
            .andReturn().response

        then:
        reused.status == 400
        json.readValue(reused.contentAsString, Map).error == "invalid_grant"
    }

    def "authorization code without PKCE is rejected"() {

        when:
        def response = mockMvc.perform(get("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "camus-web")
            .queryParam("scope", "openid task.read")
            .queryParam("redirect_uri", REDIRECT_URI)
            .with(user("camus").roles("USER")))
            .andReturn().response

        then: "PKCE 가 없으면 code 대신 오류로 redirect 한다"
        def query = UriComponentsBuilder.fromUriString(response.redirectedUrl).build().queryParams
        query.getFirst("code") == null
        query.getFirst("error") == "invalid_request"
    }

    def "login fails with a wrong password"() {

        expect:
        mockMvc.perform(formLogin().user("camus").password("wrong")).andReturn().response.redirectedUrl == "/login?error"
    }

    private MockHttpSession login(String username, String password) {

        def response = mockMvc.perform(formLogin().user(username).password(password)).andReturn()
        assert response.response.redirectedUrl == "/"
        response.request.getSession(false) as MockHttpSession
    }

    private String authorize(MockHttpSession session, String verifier) {

        def response = mockMvc.perform(get("/oauth2/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", "camus-web")
            .queryParam("scope", "openid task.read task.write")
            .queryParam("redirect_uri", REDIRECT_URI)
            .queryParam("state", "state-1")
            .queryParam("code_challenge", codeChallenge(verifier))
            .queryParam("code_challenge_method", "S256")
            .session(session))
            .andReturn().response
        def query = UriComponentsBuilder.fromUriString(response.redirectedUrl).build().queryParams
        assert query.getFirst("state") == "state-1"
        query.getFirst("code")
    }

    private Map token(Map<String, Object> params) {

        def request = post("/oauth2/token").with(httpBasic("camus-web", "camus-web-secret"))
        params.each { key, value -> request.param(key, value as String) }
        def response = mockMvc.perform(request).andReturn().response
        assert response.status == 200: response.contentAsString
        json.readValue(response.contentAsString, Map)
    }

    private NimbusJwtDecoder decoder() {

        NimbusJwtDecoder.withJwkSetUri("http://localhost:$port/oauth2/jwks").build()
    }

    private static String codeVerifier() {

        def bytes = new byte[32]
        new SecureRandom().nextBytes(bytes)
        Base64.urlEncoder.withoutPadding().encodeToString(bytes)
    }

    private static String codeChallenge(String verifier) {

        def digest = MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII))
        Base64.urlEncoder.withoutPadding().encodeToString(digest)
    }
}
