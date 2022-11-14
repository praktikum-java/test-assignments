package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(CustomNotFoundException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(HttpStatus.NOT_FOUND, e, e.getMessage(), LocalDateTime.now()).toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleValidationException(ValidationException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(Arrays.stream(e.getStackTrace()).collect(Collectors.toList()),
                HttpStatus.FORBIDDEN, e, e.getMessage(), LocalDateTime.now()).toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(Arrays.stream(e.getStackTrace()).collect(Collectors.toList()),
                HttpStatus.BAD_REQUEST, e, e.getMessage(), LocalDateTime.now()).toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleConstraintViolationException(javax.validation.ConstraintViolationException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(Arrays.stream(e.getStackTrace()).collect(Collectors.toList()),
                HttpStatus.BAD_REQUEST, e, e.getMessage(), LocalDateTime.now()).toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleConstraintViolationException(final org.hibernate.exception.ConstraintViolationException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(HttpStatus.CONFLICT, e, e.getMessage(), LocalDateTime.now()).toString();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleStatisticClientException(final StatisticSendingClientException e) {
        log.warn("{}\n{}\n{}", e, e.getMessage(), e.getStackTrace());
        return new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage(), LocalDateTime.now()).toString();
    }
}
