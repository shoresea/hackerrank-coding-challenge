package com.hackerrank.controller;

import com.hackerrank.domains.Statistics;
import com.hackerrank.service.StatisticsService;
import com.hackerrank.utils.ConversionUtil;
import com.hackerrank.validator.TransactionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/* Using Test Slices and loading just the controller specific beans in Spring Context and not others */
@WebMvcTest(TransactionStatisticsController.class)
@RunWith(SpringRunner.class)
public class TransactionStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private TransactionValidator transactionValidator;
    Statistics statistics;

    @Before
    public void setup() {
        statistics = Statistics.builder()
                .sum(new BigDecimal(101.36)).avg(new BigDecimal(25.34))
                .max(new BigDecimal(33.45)).min(new BigDecimal(10.23))
                .count(4l).build();
        given(this.statisticsService.getStatistics()).willReturn(statistics);
    }

    @Test
    public void testGetStatistics() throws Exception {
        // given
        Statistics expectedStatistics = statistics;

        // then
        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json(ConversionUtil.convertObjectToJsonString(expectedStatistics)));
    }

}
