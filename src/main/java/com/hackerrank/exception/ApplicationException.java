package com.hackerrank.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplicationException extends RuntimeException {

    private HttpStatus httpStatusCode;
    private String errorMessage;

    public ApplicationException(HttpStatus httpStatusCode, String errorMessage, Throwable cause) {
        super(cause);
        this.errorMessage = errorMessage;
        this.httpStatusCode = httpStatusCode;
    }
}
