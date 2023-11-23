package project.camus.aws.service


import spock.lang.Specification

class AwsKmsServiceTest extends Specification {

    def service = Spy(AwsKmsService)

    def "encrypt & decrypt"() {

        given:
        def plainText = "hello"

        when:
        def encrypted = service.encrypt(plainText)

        then:
        encrypted != null
        println encrypted

        when:
        def decrypted = service.decrypt(encrypted)

        then:
        plainText == decrypted
    }
}