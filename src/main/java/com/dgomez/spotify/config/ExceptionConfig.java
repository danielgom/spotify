package com.dgomez.spotify.config;

import com.dgomez.spotify.dto.ErrorResponse;
import com.dgomez.spotify.dto.ex.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionConfig extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<ErrorResponse> handleUserException(UserException ex, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .reason(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .error(ex.getStatus().name())
                .status(ex.getStatus().value())
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }
}
