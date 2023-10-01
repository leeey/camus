package project.laflex.lotto.app.util

import project.laflex.webflux.app.lotto.util.LottoHistoryUtil
import spock.lang.Specification

class LottoHistoryUtilTest extends Specification {

    def "getHistories"() {

        when:
        def histories = LottoHistoryUtil.getHistories()

        then:
        histories.size() == 1086
    }
}