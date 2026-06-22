package com.alertavecinal.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericResponseDto<T> {
    private T response;
    private ErrorMessage error;
}