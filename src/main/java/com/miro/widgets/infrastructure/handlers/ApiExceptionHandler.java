package com.miro.widgets.infrastructure.handlers;

import com.miro.widgets.domain.dto.response.ResponseErrorDto;
import com.miro.widgets.domain.dto.response.ViolationDto;
import com.miro.widgets.domain.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClientException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    private static final String UNEXPECTED_ERROR_HAPPENED = "Unexpected Error happened";
    private static final String CONSTRAINT_MESSAGE = "Constraints violations found.";
    private static final String NOTFOUND_MESSAGE = "Not found exception happened.";

    @ExceptionHandler(value = {HttpClientErrorException.class, RestClientException.class, WebClientException.class})
    public ResponseEntity<ResponseErrorDto> handleClientException(Exception ex) {
        log.error(UNEXPECTED_ERROR_HAPPENED, ex);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ResponseErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<ResponseErrorDto> handleNotFoundException(NotFoundException ex) {
        log.error(NOTFOUND_MESSAGE, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(CONSTRAINT_MESSAGE, e);
        List<ViolationDto> errors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.add(new ViolationDto(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        ResponseErrorDto response = new ResponseErrorDto(errors.get(0).getMessage(), errors);
        if (errors.size() > 1) {
            response.setMessage(CONSTRAINT_MESSAGE);
        }

        return response;
    }
}
