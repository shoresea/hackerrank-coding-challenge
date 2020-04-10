package com.hackerrank.scheduler;

import com.hackerrank.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "scheduler.statistics.enable", havingValue = "true", matchIfMissing = true)
public class StatisticsScheduler {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsScheduler(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Scheduled(fixedDelayString = "${scheduler.statistics.fixedDelay}")
    public void deleteStaleStatistics() {
        statisticsService.deleteStaleStatistics();
    }
}
