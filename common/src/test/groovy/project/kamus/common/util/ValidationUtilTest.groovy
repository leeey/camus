package project.kamus.common.util

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import project.kamus.common.exception.KamusClientException
import spock.lang.Specification


class ValidationUtilTest extends Specification {

    static class TestDto {

        @NotNull
        private Integer number

        @NotBlank
        private String comment
    }

    def "validation fail"() {

        given:
        TestDto test = new TestDto()

        when:
        ValidationUtil.validate(test)

        then:
        thrown(KamusClientException)
    }

    def "validation success"() {

        given:
        TestDto test = new TestDto(
                number: 1,
                comment: "test"
        )

        when:
        ValidationUtil.validate(test)

        then:
        noExceptionThrown()
    }
}