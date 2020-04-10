package com.hackerrank.helper;

import com.hackerrank.domains.Statistics;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class StatisticsServiceHelper {

    private static final Map<Long, Statistics> statisticsData = new ConcurrentHashMap<>();
    private static final Queue<Long> statisticsKeyQueue = new PriorityBlockingQueue<>();

    public Statistics getStatistics(Long time) {
        return statisticsData.get(time);
    }

    public Statistics getZeroStatistics() {
        return Statistics.builder()
                .sum(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP))
                .avg(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP))
                .max(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP))
                .min(BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP))
                .count(0l)
                .build();
    }

    public Statistics getInitialStatistics() {
        Statistics statistics = getZeroStatistics();
        statistics.setMax(BigDecimal.valueOf(Double.MIN_VALUE).setScale(2, BigDecimal.ROUND_HALF_UP));
        statistics.setMin(BigDecimal.valueOf(Double.MAX_VALUE).setScale(2, BigDecimal.ROUND_HALF_UP));
        return statistics;
    }

    public BigDecimal getRoundUpValue(BigDecimal bigDecimal) {
        return Optional.ofNullable(bigDecimal).orElse(BigDecimal.ZERO).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void addStatistics(Long time, Statistics statistic) {
        statisticsData.put(time, statistic);
        statisticsKeyQueue.add(time);
    }

    public void deleteStatistics() {
        statisticsData.clear();
        statisticsKeyQueue.clear();
    }

    public Queue<Long> getStatisticsQueue() {
        return statisticsKeyQueue;
    }

    public void removeStatistics(Long time) {
        statisticsData.remove(time);
    }
}
