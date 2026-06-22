package com.alertavecinal.entity;

import com.alertavecinal.enums.EstadoPatrulla;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "patrullas")
public class Patrulla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;

    private String nombre;

    private String sector;

    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    private EstadoPatrulla estado;

    private Boolean activo = true;
}
