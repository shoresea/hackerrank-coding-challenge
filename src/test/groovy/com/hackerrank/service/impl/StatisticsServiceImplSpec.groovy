package com.hackerrank.service.impl

import com.hackerrank.domains.Statistics
import com.hackerrank.domains.Transaction
import com.hackerrank.exception.ApplicationException
import com.hackerrank.helper.StatisticsServiceHelper
import com.hackerrank.service.StatisticsService
import com.hackerrank.utils.DateTimeUtil
import org.springframework.http.HttpStatus
import spock.lang.Specification


class StatisticsServiceImplSpec extends Specification {

    StatisticsService statisticsService
    Statistics zeroStatistics
    Transaction transaction
    StatisticsServiceHelper statisticsServiceHelperMock = Mock()


    def setup() {
        zeroStatistics = new Statistics(sum: 0.00, avg: 0.00, max: 0.00, min: 0.00, count: 0)
        transaction = new Transaction(amount: 29.28, timestamp: DateTimeUtil.currentTimeInUTC)
        statisticsService = new StatisticsServiceImpl(new StatisticsServiceHelper())
        statisticsService.deleteTransactions()
    }

    def "test save transaction"() {
        setup:
        def inputTransaction = transaction

        when: "getting statistics before saving transaction"
        def statisticsBeforeSaveTransaction = statisticsService.getStatistics()

        then: "we get a zero statistics result"
        statisticsBeforeSaveTransaction == zeroStatistics

        when: "we save a transaction"
        statisticsService.saveTransaction(inputTransaction)

        then: "we get a statistics with the save transaction"
        def statisticsAfterSaveTransaction = statisticsService.getStatistics()
        with(statisticsAfterSaveTransaction) {
            sum == 29.28
            avg == 29.28
            min == 29.28
            max == 29.28
            count == 1
        }
    }

    def "test get statistics"() {
        setup: "adding two transactions before getting statistics"
        def transaction1 = transaction
        def transaction2 = new Transaction(amount: 23.34, timestamp: DateTimeUtil.getRelativeTimeInUTC(-10))
        statisticsService.saveTransaction(transaction1)
        statisticsService.saveTransaction(transaction2)

        when: "we fetch the statistics"
        def statistics = statisticsService.getStatistics()

        then: "the statistics of saved transaction is obtained"
        with(statistics) {
            sum == 52.62
            avg == 26.31
            min == 23.34
            max == 29.28
            count == 2
        }
    }

    def "test delete transactions"() {
        setup:
        def transaction1 = transaction
        def transaction2 = new Transaction(amount: 23.34, timestamp: DateTimeUtil.getRelativeTimeInUTC(-10))
        statisticsService.saveTransaction(transaction1)
        statisticsService.saveTransaction(transaction2)

        when: "we fetch the statistics before deleting transaction"
        def statistics = statisticsService.getStatistics()

        then: "we get statistics with those transaction"
        with(statistics) {
            sum == 52.62
            avg == 26.31
            min == 23.34
            max == 29.28
            count == 2
        }

        when: "we delete transactions and get statistics"
        statisticsService.deleteTransactions()
        statistics = statisticsService.getStatistics()

        then: "we get a zero statistics result"
        statistics == zeroStatistics
    }

    def "test delete stale statistics"() {
        setup: "adding stale transactions"
        def transaction1 = new Transaction(amount: 23.34, timestamp: DateTimeUtil.getRelativeTimeInUTC(-55))
        statisticsService.saveTransaction(transaction1)

        when: "we delete stale statistics"
        Thread.sleep(5000)
        int deleteCount = statisticsService.deleteStaleStatistics()

        then: "stale statistics get deleted"
        deleteCount > 0
        deleteCount <= 5000
    }

    def "test save transaction with a Runtime Exception"() {
        setup:
        def inputTransaction = transaction
        statisticsService = new StatisticsServiceImpl(statisticsServiceHelperMock)

        when: "runtime exception occurs when we save a transaction"
        statisticsServiceHelperMock.getInitialStatistics() >> { throw new RuntimeException("Test Exception") }
        statisticsService.saveTransaction(inputTransaction)

        then: "we get a ApplicationException exception"
        ApplicationException thrownException = thrown()
        with(thrownException) {
            httpStatusCode == HttpStatus.INTERNAL_SERVER_ERROR
            errorMessage == "Failed to save Transaction"
        }
    }

    def "test get statistics with a Runtime Exception"() {
        setup:
        statisticsService = new StatisticsServiceImpl(statisticsServiceHelperMock)

        when: "runtime exception occurs when we get statistics"
        statisticsServiceHelperMock.getZeroStatistics() >> { throw new RuntimeException("Test Exception") }
        statisticsService.getStatistics()

        then: "we get a ApplicationException exception"
        ApplicationException thrownException = thrown()
        with(thrownException) {
            httpStatusCode == HttpStatus.INTERNAL_SERVER_ERROR
            errorMessage == "Error while getting the Statistics"
        }
    }

    def "test delete transactions with a Runtime Exception"() {
        setup:
        statisticsService = new StatisticsServiceImpl(statisticsServiceHelperMock)

        when: "runtime exception occurs when we delete transactions"
        statisticsServiceHelperMock.deleteStatistics() >> { throw new RuntimeException("Test Exception") }
        statisticsService.deleteTransactions()

        then: "we get a ApplicationException exception"
        ApplicationException thrownException = thrown()
        with(thrownException) {
            httpStatusCode == HttpStatus.INTERNAL_SERVER_ERROR
            errorMessage == "Failed to delete Transactions"
        }
    }

    def "test delete stale statistics with a Runtime Exception"() {
        setup:
        statisticsService = new StatisticsServiceImpl(statisticsServiceHelperMock)

        when: "runtime exception occurs when we get statistics"
        statisticsServiceHelperMock.getStatisticsQueue() >> null
        statisticsService.deleteStaleStatistics()

        then: "we get a ApplicationException exception"
        ApplicationException thrownException = thrown()
        with(thrownException) {
            httpStatusCode == HttpStatus.INTERNAL_SERVER_ERROR
            errorMessage == "Failed to delete Stale Statistics Data"
        }
    }
}
