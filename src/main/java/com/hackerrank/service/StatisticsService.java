package com.hackerrank.service;

import com.hackerrank.domains.Statistics;
import com.hackerrank.domains.Transaction;
import com.hackerrank.exception.ApplicationException;

public interface StatisticsService {

    Statistics getStatistics() throws ApplicationException;

    void saveTransaction(Transaction transaction) throws ApplicationException;

    void deleteTransactions() throws ApplicationException;

    int deleteStaleStatistics() throws ApplicationException;

}
