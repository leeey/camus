package project.camus.lotto.app.util

import project.camus.webflux.api.lotto.util.LottoHistoryUtil
import spock.lang.Specification

class LottoHistoryUtilTest extends Specification {

    def "getHistories"() {

        when:
        def histories = LottoHistoryUtil.getHistories()

        then:
        histories.size() == 1086
    }
}