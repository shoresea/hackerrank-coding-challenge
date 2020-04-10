package com.hackerrank.validator

import com.hackerrank.domains.Statistics
import com.hackerrank.domains.Transaction
import com.hackerrank.exception.ApplicationException
import com.hackerrank.utils.DateTimeUtil
import org.springframework.http.HttpStatus
import org.springframework.validation.Errors
import spock.lang.Specification

class TransactionValidatorSpec extends Specification {

    Errors errorsMock = Mock()
    Transaction transaction
    TransactionValidator transactionValidator

    def setup() {
        transactionValidator = new TransactionValidator()
        transaction = new Transaction(amount: 29.28, timestamp: DateTimeUtil.currentTimeInUTC)
    }

    def "test isAssignable for TransactionValidator"() {
        when:
        def transactionFlag = transactionValidator.supports(Transaction.class)
        def statisticsFlag = transactionValidator.supports(Statistics.class)

        then:
        transactionFlag == true
        statisticsFlag == false
    }

    def "validate with null Transaction"() {
        when:
        transactionValidator.validate(null, errorsMock)

        then:
        ApplicationException thrownException = thrown()
        thrownException.httpStatusCode == HttpStatus.BAD_REQUEST
        thrownException.errorMessage == "Request Body cannot be empty"
    }

    def "validate with null amount"() {
        given:
        transaction.setAmount(null)

        when:
        transactionValidator.validate(transaction, errorsMock)

        then:
        ApplicationException thrownException = thrown()
        thrownException.httpStatusCode == HttpStatus.BAD_REQUEST
        thrownException.errorMessage == "Amount cannot be empty"
    }

    def "validate with null timestamp"() {
        given:
        transaction.setTimestamp(null)

        when:
        transactionValidator.validate(transaction, errorsMock)

        then:
        ApplicationException thrownException = thrown()
        thrownException.httpStatusCode == HttpStatus.BAD_REQUEST
        thrownException.errorMessage == "Timestamp cannot be empty"
    }

    def "validate with future timestamp"() {
        given:
        transaction.setTimestamp(DateTimeUtil.getRelativeTimeInUTC(10))

        when:
        transactionValidator.validate(transaction, errorsMock)

        then:
        ApplicationException thrownException = thrown()
        thrownException.httpStatusCode == HttpStatus.UNPROCESSABLE_ENTITY
        thrownException.errorMessage == "Timestamp cannot be in the future"
    }

    def "validate with timestamp older than 60 seconds"() {
        given:
        transaction.setTimestamp(DateTimeUtil.getRelativeTimeInUTC(-100))

        when:
        transactionValidator.validate(transaction, errorsMock)

        then:
        ApplicationException thrownException = thrown()
        thrownException.httpStatusCode == HttpStatus.NO_CONTENT
        thrownException.errorMessage == "Timestamp cannot be older than 60 seconds"
    }

}
