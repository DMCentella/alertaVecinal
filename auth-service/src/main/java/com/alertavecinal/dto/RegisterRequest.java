package com.alertavecinal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class RegisterRequest {
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    private String rol;
}
