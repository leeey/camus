package project.camus.common.util

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import project.camus.common.exception.CamusClientException
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
        thrown(CamusClientException)
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