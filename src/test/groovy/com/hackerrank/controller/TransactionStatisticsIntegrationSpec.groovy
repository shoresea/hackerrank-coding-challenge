package com.hackerrank.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.hackerrank.domains.Statistics
import com.hackerrank.domains.Transaction
import com.hackerrank.exception.ApplicationExceptionHandler
import com.hackerrank.service.StatisticsService
import com.hackerrank.utils.ConversionUtil
import com.hackerrank.utils.DateTimeUtil
import com.hackerrank.validator.TransactionValidator
import org.apache.commons.lang3.StringUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup

class TransactionStatisticsIntegrationSpec extends Specification {

    private StatisticsService statisticsService = Mock()
    private TransactionStatisticsController transactionStatisticsController
    private TransactionValidator transactionValidator

    private MockMvc mockMvc
    Statistics statistics
    Transaction transaction

    def setup() {
        statistics = new Statistics(sum: 101.36, avg: 25.34, max: 33.45, min: 10.23, count: 4)
        transaction = new Transaction(amount: 29.28, timestamp: DateTimeUtil.currentTimeInUTC)
        transactionValidator = new TransactionValidator()
        transactionStatisticsController = new TransactionStatisticsController(statisticsService, transactionValidator)
        mockMvc = standaloneSetup(transactionStatisticsController).setControllerAdvice(new ApplicationExceptionHandler()).build()
    }

    def "test get Statistics endpoint"() {
        given:
        def expectedStatistics = statistics

        when:
        def response = mockMvc.perform(get('/statistics')).andReturn().response

        then:
        1 * statisticsService.getStatistics() >> statistics
        response.status == HttpStatus.OK.value()
        Statistics actualStatistics = ConversionUtil.convertStringToObject(response.contentAsString, new TypeReference<Statistics>() {
        })
        expectedStatistics == actualStatistics
    }

    def "test save transaction endpoint with current timestamp"() {
        given:
        def inputTransaction = transaction

        when:
        def response = mockMvc.perform(post('/transactions')
                .content(ConversionUtil.convertObjectToJsonString(inputTransaction)).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().response

        then:
        1 * statisticsService.saveTransaction(inputTransaction)
        response.status == HttpStatus.CREATED.value()
    }

    def "test delete transactions endpoint"() {
        when:
        def response = mockMvc.perform(delete('/transactions')).andReturn().response

        then:
        1 * statisticsService.deleteTransactions()
        response.status == HttpStatus.NO_CONTENT.value()
    }

    def "test save transaction endpoint with timestamp before 60 seconds"() {
        given:
        def inputTransaction = transaction
        inputTransaction.setTimestamp(DateTimeUtil.getRelativeTimeInUTC(-100))

        when:
        def response = mockMvc.perform(post('/transactions')
                .content(ConversionUtil.convertObjectToJsonString(inputTransaction))
                .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().response

        then:
        0 * statisticsService.saveTransaction(inputTransaction)
        response.status == HttpStatus.NO_CONTENT.value()
        response.contentAsString == StringUtils.EMPTY
    }

    def "test save transaction endpoint with future timestamp"() {
        given:
        def inputTransaction = transaction
        inputTransaction.setTimestamp(DateTimeUtil.getRelativeTimeInUTC(10))

        when:
        def response = mockMvc.perform(post('/transactions')
                .content(ConversionUtil.convertObjectToJsonString(inputTransaction))
                .contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().response

        then:
        0 * statisticsService.saveTransaction(inputTransaction)
        response.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
        response.contentAsString == StringUtils.EMPTY
    }

    def "test save transaction endpoint with invalid JSON"() {
        given:
        def inputTransaction = transaction

        when:
        def response = mockMvc.perform(post('/transactions')
                .content(ConversionUtil.convertObjectToJsonString(inputTransaction).substring(0, 20)).contentType(MediaType.APPLICATION_JSON_VALUE).accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn().response

        then:
        0 * statisticsService.saveTransaction(_)
        response.status == HttpStatus.BAD_REQUEST.value()
        response.contentAsString == StringUtils.EMPTY
    }

}
