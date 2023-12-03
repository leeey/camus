package project.camus.aws.client

import project.camus.aws.client.builder.AwsKmsBuilder
import spock.lang.Specification

class AwsKmsClientTest extends Specification {

    def builder = Spy(AwsKmsBuilder)
    AwsKmsClient client = Spy(AwsKmsClient, constructorArgs: [builder]) as AwsKmsClient

    def "encrypt & decrypt"() {

        given:
        def word = "hello"

        when:
        def cipher = client.encrypt(word)

        then:
        1 * builder.build()
        cipher != null

        when:
        def plain = client.decrypt(cipher)

        then:
        1 * builder.build()
        plain == word
    }
}