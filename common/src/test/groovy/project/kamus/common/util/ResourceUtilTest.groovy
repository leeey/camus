package project.kamus.common.util


import org.springframework.util.FileCopyUtils
import project.kamus.common.exception.KamusClientException
import spock.lang.Specification

import java.nio.charset.StandardCharsets

class ResourceUtilTest extends Specification {

    def "path is null"() {

        given:
        def path = null

        when:
        ResourceUtil.getPathResource(path)

        then:
        def e = thrown(KamusClientException)
        e.cause.message == "path is invalid."
    }

    def "path is blank"() {

        given:
        def path = "     "

        when:
        ResourceUtil.getPathResource(path)

        then:
        def e = thrown(KamusClientException)
        e.cause.message == "path is invalid."
    }

    def "path is incorrect"() {

        given:
        def path = "test.json"

        when:
        def resource = ResourceUtil.getPathResource(path)

        then:
        !resource.exists()
    }

    def "path is correct"() {

        given:
        def path = "test.txt"

        when:
        def resource = ResourceUtil.getPathResource(path)

        then:
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            def text = FileCopyUtils.copyToString(reader)
            text == "Hello!"
        } catch (IOException e) {
            throw new UncheckedIOException(e)
        }
    }
}