package ru.practicum.exception;

import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@ToString
public class ApiError {
    public ApiError(List<StackTraceElement> errors, HttpStatus status, Throwable reason, String message, LocalDateTime timestamp) {
        this.errors = errors;
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    public ApiError(HttpStatus status, Throwable reason, String message, LocalDateTime timestamp) {
        this.message = message;
        this.reason = reason;
        this.status = status;
        this.timestamp = timestamp;
    }

    private List<StackTraceElement> errors;            // Список стектрейсов или описания ошибок
    private final String message;                 // Сообщение об ошибке
    private final Throwable reason;                  // Общее описание причины ошибки
    private final HttpStatus status;                  // !!!ENUM!!!  Код статуса HTTP-ответа
    private final LocalDateTime timestamp;        // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
}
