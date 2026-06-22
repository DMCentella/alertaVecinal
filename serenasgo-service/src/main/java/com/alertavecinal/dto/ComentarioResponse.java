package com.alertavecinal.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComentarioResponse {

    private Long id;
    private Long incidenteId;
    private Long usuarioId;
    private String comentario;
    private LocalDateTime fecha;
}
