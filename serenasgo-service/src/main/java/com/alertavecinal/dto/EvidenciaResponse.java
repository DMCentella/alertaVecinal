package com.alertavecinal.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EvidenciaResponse {

    private Long id;
    private Long incidenteId;
    private String nombreArchivo;
    private String rutaArchivo;
    private LocalDateTime fechaSubida;
}
