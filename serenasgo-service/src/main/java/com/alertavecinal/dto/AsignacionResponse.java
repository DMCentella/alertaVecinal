package com.alertavecinal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionResponse {

    private Long id;
    private Long incidenteId;
    private Long patrullaId;
    private Long supervisorId;
    private LocalDateTime fechaAsignacion;
}
