package project.camus.aws.client

import project.camus.aws.builder.AwsKmsBuilder
import spock.lang.Specification

class AwsKmsClientTest extends Specification {

    def builder = Spy(AwsKmsBuilder)
    def client = Spy(AwsKmsClient, constructorArgs: [builder])

    def "encrypt & decrypt"() {

        given:
        def plainText = "hello"

        when:
        def encrypted = client.encrypt(plainText)

        then:
        encrypted != null
        println encrypted

        when:
        def decrypted = client.decrypt(encrypted)

        then:
        plainText == decrypted
    }
}