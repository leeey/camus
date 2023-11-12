package project.camus.springcloud.config

import project.camus.springcloud.config.util.AwsKmsUtil
import spock.lang.Specification


class AwsKmsUtilTest extends Specification {

    def "encrypt & decrypt"() {

        given:
        def plainText = "hello"

        when:
        def encrypted = AwsKmsUtil.encrypt(plainText)

        then:
        encrypted != null
        println ""
        println encrypted
        println ""

        when:
        def decrypted = AwsKmsUtil.decrypt(encrypted)

        then:
        plainText == decrypted
    }
}