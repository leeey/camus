package project.kamus.lotto.app.util

import project.kamus.webflux.app.lotto.util.LottoHistoryUtil
import spock.lang.Specification

class LottoHistoryUtilTest extends Specification {

    def "getHistories"() {

        when:
        def histories = LottoHistoryUtil.getHistories()

        then:
        histories.size() == 1086
    }
}