package com.hackerrank.service.impl;

import com.hackerrank.domains.Statistics;
import com.hackerrank.domains.Transaction;
import com.hackerrank.exception.ApplicationException;
import com.hackerrank.helper.StatisticsServiceHelper;
import com.hackerrank.service.StatisticsService;
import com.hackerrank.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

  private final StatisticsServiceHelper statisticsServiceHelper;

  @Autowired
  public StatisticsServiceImpl(StatisticsServiceHelper statisticsServiceHelper) {
    this.statisticsServiceHelper = statisticsServiceHelper;
  }

  @Override
  public Statistics getStatistics() throws ApplicationException {
    try {
      synchronized (statisticsServiceHelper) {
        Long currentTime = DateTimeUtil.getCurrentTimeInUTC().getTime();
        return Optional.ofNullable(statisticsServiceHelper.getStatistics(currentTime))
            .orElse(statisticsServiceHelper.getZeroStatistics());
      }
    } catch (Exception ex) {
      LOGGER.error("Error while getting the Statistics ", ex);
      throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while getting the Statistics", ex);
    }
  }

  @Override
  public void saveTransaction(Transaction transaction) throws ApplicationException {
    try {
      Long currentTime = DateTimeUtil.getCurrentTimeInUTC().getTime();
      Long transactionTime = transaction.getTimestamp().getTime();
      synchronized (statisticsServiceHelper) {
        long bound = transactionTime + 60000l;
        for (long time = currentTime; time < bound; time++) {
          Statistics statistic = statisticsServiceHelper.getStatistics(time);
          if (Objects.isNull(statistic)) {
            statistic = statisticsServiceHelper.getInitialStatistics();
            statisticsServiceHelper.addStatistics(time, statistic);
          }
          statistic.setSum(statisticsServiceHelper.getRoundUpValue(statistic.getSum().add(transaction.getAmount())));
          statistic.setCount(statistic.getCount() + 1l);
          if (transaction.getAmount().compareTo(statistic.getMax()) > 0)
            statistic.setMax(statisticsServiceHelper.getRoundUpValue(transaction.getAmount()));
          if (transaction.getAmount().compareTo(statistic.getMin()) < 0)
            statistic.setMin(statisticsServiceHelper.getRoundUpValue(transaction.getAmount()));
        }
      }
    } catch (Exception ex) {
      LOGGER.error("Error while saving the Transaction Amount:{}, Time:{} ", transaction.getAmount(),
          transaction.getTimestamp(), ex);
      throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save Transaction", ex);
    }
  }

  @Override
  public void deleteTransactions() throws ApplicationException {
    try {
      synchronized (statisticsServiceHelper) {
        statisticsServiceHelper.deleteStatistics();
      }
    } catch (Exception ex) {
      LOGGER.error("Error while deleting the Transactions ", ex);
      throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete Transactions", ex);
    }
  }

  @Override
  public int deleteStaleStatistics() {
    try {
      Queue<Long> statisticsKeyQueue = statisticsServiceHelper.getStatisticsQueue();
      int queueCount = statisticsKeyQueue.size();
      Long currentTime = DateTimeUtil.getCurrentTimeInUTC().getTime();
      synchronized (statisticsServiceHelper) {
        while (!statisticsKeyQueue.isEmpty() && statisticsKeyQueue.peek() < currentTime) {
          statisticsServiceHelper.removeStatistics(statisticsKeyQueue.poll());
        }
        int deleteCount = queueCount - statisticsKeyQueue.size();
        LOGGER.info("Deleted {} stale Statistics data ", deleteCount);
        return deleteCount;
      }
    } catch (Exception ex) {
      LOGGER.error("Error while deleting the Stale Statistics Data ", ex);
      throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete Stale Statistics Data", ex);
    }
  }

}
