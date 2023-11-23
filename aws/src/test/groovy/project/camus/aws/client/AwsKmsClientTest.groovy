package project.camus.aws.client


import spock.lang.Specification

class AwsKmsClientTest extends Specification {

    def client = Spy(AwsKmsClient)

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