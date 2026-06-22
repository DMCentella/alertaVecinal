package com.alertavecinal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsignacionRequest {

    @NotNull(message = "El ID del incidente es obligatorio")
    private Long incidenteId;

    @NotNull(message = "El ID de la patrulla es obligatorio")
    private Long patrullaId;
}
