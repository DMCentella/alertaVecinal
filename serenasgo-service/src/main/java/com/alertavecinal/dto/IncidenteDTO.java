package com.alertavecinal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenteDTO {

    private Long id;

    private String tipo;

    private String descripcion;

    private String direccion;

    private String estado;

    private Long usuarioId;

    private Boolean activo;
}
