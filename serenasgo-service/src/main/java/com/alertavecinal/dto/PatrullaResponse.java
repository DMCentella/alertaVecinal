package com.alertavecinal.dto;

import com.alertavecinal.enums.EstadoPatrulla;

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
public class PatrullaResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String sector;
    private Long usuarioId;
    private EstadoPatrulla estado;
    private String username;
    private String password;
}
