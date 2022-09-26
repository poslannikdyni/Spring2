package com.edu.ulab.app.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ServiceException extends RuntimeException {
    private HttpStatus httpStatus;

    public ServiceException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.httpStatus = httpStatus;
    }
}
