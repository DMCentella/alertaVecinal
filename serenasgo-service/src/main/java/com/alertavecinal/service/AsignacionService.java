package com.alertavecinal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alertavecinal.client.IncidentClient;
import com.alertavecinal.dto.AsignacionRequest;
import com.alertavecinal.dto.AsignacionResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.IncidenteDTO;
import com.alertavecinal.dto.ReasignarRequest;
import com.alertavecinal.entity.AsignacionIncidente;
import com.alertavecinal.entity.HistorialEstado;
import com.alertavecinal.entity.Patrulla;
import com.alertavecinal.enums.EstadoIncidente;
import com.alertavecinal.enums.EstadoPatrulla;
import com.alertavecinal.exception.ResourceNotFoundException;
import com.alertavecinal.repository.AsignacionIncidenteRepository;
import com.alertavecinal.repository.HistorialEstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private final AsignacionIncidenteRepository asignacionRepository;
    private final PatrullaService patrullaService;
    private final IncidentClient incidentClient;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final EventPublisherService eventPublisherService;

    @Transactional
    public AsignacionResponse crearAsignacion(AsignacionRequest request, Long supervisorId) {
        IncidenteDTO incidente = obtenerIncidente(request.getIncidenteId());

        if (incidente == null) {
            throw new ResourceNotFoundException(
                    "El incidente con ID = " + request.getIncidenteId() + " no existe.");
        }

        if (!EstadoIncidente.PENDIENTE.name().equals(incidente.getEstado())) {
            throw new IllegalStateException(
                    "El incidente debe estar en estado PENDIENTE para ser asignado. Estado actual: "
                            + incidente.getEstado());
        }

        Patrulla patrulla = patrullaService.obtenerPatrulla(request.getPatrullaId());
        if (patrulla.getEstado() != EstadoPatrulla.DISPONIBLE) {
            throw new IllegalStateException(
                    "La patrulla no está DISPONIBLE. Estado actual: " + patrulla.getEstado());
        }

        EstadoIncidente estadoAnterior = EstadoIncidente.valueOf(incidente.getEstado());
        incidente.setEstado(EstadoIncidente.ASIGNADO.name());
        incidentClient.actualizarIncidente(request.getIncidenteId(), incidente);

        patrulla.setEstado(EstadoPatrulla.ATENDIENDO);
        patrullaService.guardarPatrulla(patrulla);

        AsignacionIncidente asignacion = new AsignacionIncidente();
        asignacion.setIncidenteId(request.getIncidenteId());
        asignacion.setPatrullaId(request.getPatrullaId());
        asignacion.setSupervisorId(supervisorId);
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacion = asignacionRepository.save(asignacion);

        guardarHistorial(request.getIncidenteId(), supervisorId,
                estadoAnterior, EstadoIncidente.ASIGNADO);

        eventPublisherService.publicarIncidenteAsignado(
                request.getIncidenteId(), supervisorId, request.getPatrullaId());

        return AsignacionResponse.builder()
                .id(asignacion.getId())
                .incidenteId(asignacion.getIncidenteId())
                .patrullaId(asignacion.getPatrullaId())
                .supervisorId(asignacion.getSupervisorId())
                .fechaAsignacion(asignacion.getFechaAsignacion())
                .build();
    }

    public List<AsignacionResponse> listarAsignaciones() {
        return asignacionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AsignacionResponse obtenerAsignacion(Long id) {
        AsignacionIncidente asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La asignación con ID = " + id + " no existe."));
        return toResponse(asignacion);
    }

    public AsignacionResponse reasignarIncidente(Long asignacionId, ReasignarRequest request,
                                                  Long supervisorId) {
        AsignacionIncidente asignacion = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La asignación con ID = " + asignacionId + " no existe."));

        Patrulla patrullaAnterior = patrullaService.obtenerPatrulla(asignacion.getPatrullaId());
        Patrulla nuevaPatrulla = patrullaService.obtenerPatrulla(request.getNuevaPatrullaId());

        if (nuevaPatrulla.getEstado() != EstadoPatrulla.DISPONIBLE) {
            throw new IllegalStateException(
                    "La nueva patrulla no está DISPONIBLE. Estado actual: " + nuevaPatrulla.getEstado());
        }

        IncidenteDTO incidente = obtenerIncidente(asignacion.getIncidenteId());

        asignacion.setPatrullaId(request.getNuevaPatrullaId());
        asignacion.setSupervisorId(supervisorId);
        asignacion.setFechaAsignacion(LocalDateTime.now());
        asignacionRepository.save(asignacion);

        nuevaPatrulla.setEstado(EstadoPatrulla.ATENDIENDO);
        patrullaService.guardarPatrulla(nuevaPatrulla);

        if (!tieneIncidentesActivos(patrullaAnterior.getId())) {
            patrullaAnterior.setEstado(EstadoPatrulla.DISPONIBLE);
            patrullaService.guardarPatrulla(patrullaAnterior);
        }

        return toResponse(asignacion);
    }

    public boolean tieneIncidentesActivos(Long patrullaId) {
        List<AsignacionIncidente> asignaciones = asignacionRepository.findByPatrullaId(patrullaId);
        for (AsignacionIncidente a : asignaciones) {
            IncidenteDTO incidente = obtenerIncidente(a.getIncidenteId());
            if (incidente != null
                    && !EstadoIncidente.CERRADO.name().equals(incidente.getEstado())) {
                return true;
            }
        }
        return false;
    }

    public List<IncidenteDTO> obtenerIncidentesDePatrulla(Long patrullaId) {
        List<AsignacionIncidente> asignaciones = asignacionRepository.findByPatrullaId(patrullaId);
        return asignaciones.stream()
                .map(a -> obtenerIncidente(a.getIncidenteId()))
                .collect(Collectors.toList());
    }

    private IncidenteDTO obtenerIncidente(Long incidenteId) {
        GenericResponseDto<IncidenteDTO> response = incidentClient.obtenerIncidente(incidenteId);
        return response != null ? response.getResponse() : null;
    }

    private AsignacionResponse toResponse(AsignacionIncidente a) {
        return AsignacionResponse.builder()
                .id(a.getId())
                .incidenteId(a.getIncidenteId())
                .patrullaId(a.getPatrullaId())
                .supervisorId(a.getSupervisorId())
                .fechaAsignacion(a.getFechaAsignacion())
                .build();
    }

    private void guardarHistorial(Long incidenteId, Long usuarioId,
                                   EstadoIncidente estadoAnterior, EstadoIncidente estadoNuevo) {
        HistorialEstado historial = new HistorialEstado();
        historial.setIncidenteId(incidenteId);
        historial.setSerenazgoId(usuarioId);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(LocalDateTime.now());
        historialEstadoRepository.save(historial);
    }
}
