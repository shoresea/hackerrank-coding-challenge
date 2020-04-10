package com.hackerrank.validator;

import com.hackerrank.domains.Transaction;
import com.hackerrank.exception.ApplicationException;
import com.hackerrank.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Slf4j
@Component
public class TransactionValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return Transaction.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) throws ApplicationException {
        Transaction transaction = (Transaction) o;
        if (Objects.nonNull(transaction)) {
            if (Objects.isNull(transaction.getAmount())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Amount cannot be empty", null);
            }
            if (Objects.isNull(transaction.getTimestamp())) {
                throw new ApplicationException(HttpStatus.BAD_REQUEST, "Timestamp cannot be empty", null);
            }
            if (transaction.getTimestamp().getTime() < DateTimeUtil.getRelativeTimeInUTC(-60l).getTime()) {
                throw new ApplicationException(HttpStatus.NO_CONTENT, "Timestamp cannot be older than 60 seconds", null);
            }
            if (transaction.getTimestamp().getTime() > DateTimeUtil.getCurrentTimeInUTC().getTime()) {
                throw new ApplicationException(HttpStatus.UNPROCESSABLE_ENTITY, "Timestamp cannot be in the future", null);
            }
        } else {
            throw new ApplicationException(HttpStatus.BAD_REQUEST, "Request Body cannot be empty", null);
        }
    }
}
