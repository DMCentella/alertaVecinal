package com.alertavecinal.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ErrorMessage {
    private Integer statusCode;
    private String message;
    private LocalDate dateError;
}
