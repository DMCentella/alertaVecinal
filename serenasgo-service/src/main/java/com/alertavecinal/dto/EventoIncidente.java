package com.alertavecinal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoIncidente {

    private String tipoEvento;
    private Long incidenteId;
    private Long usuarioId;
    private String estado;
    private String descripcion;
    private LocalDateTime fecha;
}
