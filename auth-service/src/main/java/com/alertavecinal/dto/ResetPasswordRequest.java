package com.alertavecinal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank(message = "La nueva contraseña es obligatoria")
    private String nuevaPassword;
}
