package com.alertavecinal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alertavecinal.dto.EventoIncidente;

@Component
public class EventListenerService {

    private static final Logger log = LoggerFactory.getLogger(EventListenerService.class);

    @RabbitListener(queues = "incidente.creado")
    public void handleIncidenteCreado(EventoIncidente evento) {
        log.info("[EVENTO] Incidente creado: id={}, usuario={}, fecha={}",
                evento.getIncidenteId(), evento.getUsuarioId(), evento.getFecha());
    }

    @RabbitListener(queues = "incidente.asignado")
    public void handleIncidenteAsignado(EventoIncidente evento) {
        log.info("[EVENTO] Incidente asignado: id={}, usuario={}, fecha={}",
                evento.getIncidenteId(), evento.getUsuarioId(), evento.getFecha());
    }

    @RabbitListener(queues = "incidente.actualizado")
    public void handleIncidenteActualizado(EventoIncidente evento) {
        log.info("[EVENTO] Incidente actualizado: id={}, estado={}, fecha={}",
                evento.getIncidenteId(), evento.getEstado(), evento.getFecha());
    }

    @RabbitListener(queues = "incidente.cerrado")
    public void handleIncidenteCerrado(EventoIncidente evento) {
        log.info("[EVENTO] Incidente cerrado: id={}, usuario={}, fecha={}",
                evento.getIncidenteId(), evento.getUsuarioId(), evento.getFecha());
    }
}
