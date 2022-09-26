package com.edu.ulab.app.utility;

import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Slf4j
@Component
public class ExceptionUtility {

    public void throwServiceExceptionIfNull(Object test, String msg, HttpStatus httpStatus) {
        if (test == null) {
            throw new ServiceException(msg, httpStatus);
        }
    }
    public void throwServiceExceptionIfNull(Object test, String msg) {
        throwServiceExceptionIfNull(test, msg, HttpStatus.BAD_REQUEST);
    }

    public void throwServiceException(Exception e, String msg) {
//        log.error(msg, e);
        throw new ServiceException(msg, HttpStatus.BAD_REQUEST);
    }

    public void throwServiceException( String msg) {
        throw new ServiceException(msg, HttpStatus.BAD_REQUEST);
    }

    public void throwNotFoundExceptionIfNull(Object test, String msg) {
        if (test == null) {
            throw new NotFoundException(msg);
        }
    }

    public <E> void throwServiceExceptionIf(E object, Predicate<E> condition, String msg) {
        if (condition.test(object)) {
            throw new ServiceException(msg, HttpStatus.BAD_REQUEST);
        }
    }


    public void throwNotFoundException(String msg) {
        throw new NotFoundException(msg);
    }
}
