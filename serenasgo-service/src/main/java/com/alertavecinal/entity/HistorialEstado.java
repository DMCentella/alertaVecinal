package com.alertavecinal.entity;

import java.time.LocalDateTime;

import com.alertavecinal.enums.EstadoIncidente;

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
@Table(name = "historial_estados")
public class HistorialEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long incidenteId;

    private Long serenazgoId;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estadoAnterior;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estadoNuevo;

    private LocalDateTime fechaCambio;
}