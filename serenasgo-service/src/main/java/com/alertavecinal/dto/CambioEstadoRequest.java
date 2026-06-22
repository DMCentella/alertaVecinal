package com.alertavecinal.dto;

import com.alertavecinal.enums.EstadoIncidente;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambioEstadoRequest {

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoIncidente nuevoEstado;

}
