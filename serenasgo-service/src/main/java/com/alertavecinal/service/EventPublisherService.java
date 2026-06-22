package com.alertavecinal.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.alertavecinal.config.RabbitMQConfig;
import com.alertavecinal.dto.EventoIncidente;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final RabbitTemplate rabbitTemplate;

    public void publicarIncidenteCreado(Long incidenteId, Long usuarioId, String descripcion) {
        EventoIncidente evento = EventoIncidente.builder()
                .tipoEvento("INCIDENTE_CREADO")
                .incidenteId(incidenteId)
                .usuarioId(usuarioId)
                .estado("PENDIENTE")
                .descripcion(descripcion)
                .fecha(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "incidente.creado", evento);
    }

    public void publicarIncidenteAsignado(Long incidenteId, Long usuarioId, Long patrullaId) {
        EventoIncidente evento = EventoIncidente.builder()
                .tipoEvento("INCIDENTE_ASIGNADO")
                .incidenteId(incidenteId)
                .usuarioId(usuarioId)
                .estado("ASIGNADO")
                .descripcion("Asignado a patrulla " + patrullaId)
                .fecha(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "incidente.asignado", evento);
    }

    public void publicarIncidenteActualizado(Long incidenteId, Long usuarioId, String estado) {
        EventoIncidente evento = EventoIncidente.builder()
                .tipoEvento("INCIDENTE_ACTUALIZADO")
                .incidenteId(incidenteId)
                .usuarioId(usuarioId)
                .estado(estado)
                .descripcion("Estado actualizado a " + estado)
                .fecha(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "incidente.actualizado", evento);
    }

    public void publicarIncidenteCerrado(Long incidenteId, Long usuarioId) {
        EventoIncidente evento = EventoIncidente.builder()
                .tipoEvento("INCIDENTE_CERRADO")
                .incidenteId(incidenteId)
                .usuarioId(usuarioId)
                .estado("CERRADO")
                .descripcion("Incidente cerrado")
                .fecha(LocalDateTime.now())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "incidente.cerrado", evento);
    }
}
