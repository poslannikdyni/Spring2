package com.edu.ulab.app.web.handler;

import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.ServiceException;
import com.edu.ulab.app.web.response.BaseWebResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler({NotFoundException.class, ServiceException.class})
    public ResponseEntity<BaseWebResponse> handleException(@NonNull final Exception exc) {
        HttpStatus httpStatus;
        if (exc instanceof ServiceException e) {
            httpStatus = e.getHttpStatus();
        } else if (exc instanceof NotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(httpStatus)
                .body(new BaseWebResponse(createErrorMessage(exc)));
    }

    private String createErrorMessage(Exception exception) {
        final String message = exception.getMessage();
        log.error(ExceptionHandlerUtils.buildErrorMessage(exception));
        return message;
    }
}
