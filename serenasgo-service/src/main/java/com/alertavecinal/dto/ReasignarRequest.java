package com.alertavecinal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReasignarRequest {

    @NotNull(message = "El ID de la nueva patrulla es obligatorio")
    private Long nuevaPatrullaId;
}
