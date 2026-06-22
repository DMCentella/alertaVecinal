package com.alertavecinal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComentarioRequest {

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;
}
