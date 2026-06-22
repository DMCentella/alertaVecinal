package com.alertavecinal.exception;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alertavecinal.dto.ErrorMessage;
import com.alertavecinal.dto.GenericResponseDto;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public GenericResponseDto<Void> handleResourceNotFoundException(
            ResourceNotFoundException e) {
        ErrorMessage error = ErrorMessage.builder()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .dateError(LocalDate.now())
                .build();
        return GenericResponseDto.<Void>builder()
                .error(error)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponseDto<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErrorMessage errorMessage = ErrorMessage.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("Error de validación")
                .dateError(LocalDate.now())
                .build();

        GenericResponseDto<Map<String, String>> response =
                GenericResponseDto.<Map<String, String>>builder()
                        .response(errors)
                        .error(errorMessage)
                        .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
