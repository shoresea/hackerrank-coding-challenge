package com.hackerrank.controller;

import com.hackerrank.domains.Statistics;
import com.hackerrank.domains.Transaction;
import com.hackerrank.exception.ApplicationException;
import com.hackerrank.service.StatisticsService;
import com.hackerrank.validator.TransactionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionStatisticsController {

    private final StatisticsService statisticsService;
    private final TransactionValidator transactionValidator;

    @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Statistics getStatistics() throws ApplicationException {
        return statisticsService.getStatistics();
    }

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(value = "/transactions", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveTransaction(@RequestBody Transaction transaction, BindingResult bindingResult) throws ApplicationException {
        transactionValidator.validate(transaction, bindingResult);
        statisticsService.saveTransaction(transaction);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/transactions")
    public void deleteTransactions() throws ApplicationException {
        statisticsService.deleteTransactions();
    }

}
