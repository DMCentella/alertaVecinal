package com.alertavecinal.dto;

import java.time.LocalDateTime;

import com.alertavecinal.enums.EstadoIncidente;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistorialEstadoResponse {

    private Long id;

    private Long incidenteId;

    private Long serenazgoId;

    private EstadoIncidente estadoAnterior;

    private EstadoIncidente estadoNuevo;

    private LocalDateTime fechaCambio;
}