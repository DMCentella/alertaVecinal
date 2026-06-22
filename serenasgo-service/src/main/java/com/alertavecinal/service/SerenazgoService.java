package com.alertavecinal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alertavecinal.client.IncidentClient;
import com.alertavecinal.dto.CambioEstadoRequest;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.HistorialEstadoResponse;
import com.alertavecinal.dto.IncidenteDTO;
import com.alertavecinal.entity.AsignacionIncidente;
import com.alertavecinal.entity.HistorialEstado;
import com.alertavecinal.entity.Patrulla;
import com.alertavecinal.enums.EstadoIncidente;
import com.alertavecinal.enums.EstadoPatrulla;
import com.alertavecinal.repository.AsignacionIncidenteRepository;
import com.alertavecinal.repository.HistorialEstadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SerenazgoService {

    private final IncidentClient incidentClient;
    private final HistorialEstadoRepository historialEstadoRepository;
    private final AsignacionIncidenteRepository asignacionIncidenteRepository;
    private final PatrullaService patrullaService;
    private final EventPublisherService eventPublisherService;

    private static final Map<EstadoIncidente, List<EstadoIncidente>> TRANSICIONES_PERMITIDAS = Map.of(
            EstadoIncidente.PENDIENTE, List.of(EstadoIncidente.ASIGNADO),
            EstadoIncidente.ASIGNADO, List.of(EstadoIncidente.EN_PROCESO),
            EstadoIncidente.EN_PROCESO, List.of(EstadoIncidente.ATENDIDO),
            EstadoIncidente.ATENDIDO, List.of(EstadoIncidente.CERRADO),
            EstadoIncidente.CERRADO, List.of()
    );

    public List<IncidenteDTO> listarIncidentes() {
        GenericResponseDto<List<IncidenteDTO>> response =
                incidentClient.listarIncidentes();
        return response.getResponse();
    }

    public IncidenteDTO obtenerIncidente(Long id) {
        GenericResponseDto<IncidenteDTO> response =
                incidentClient.obtenerIncidente(id);
        return response.getResponse();
    }

    public List<IncidenteDTO> listarMisIncidentes(Long usuarioId) {
        Patrulla patrulla = patrullaService.obtenerPatrullaPorUsuario(usuarioId);
        List<AsignacionIncidente> asignaciones =
                asignacionIncidenteRepository.findByPatrullaId(patrulla.getId());
        return asignaciones.stream()
                .map(a -> {
                    GenericResponseDto<IncidenteDTO> response =
                            incidentClient.obtenerIncidente(a.getIncidenteId());
                    return response != null ? response.getResponse() : null;
                })
                .filter(i -> i != null)
                .toList();
    }

    @Transactional
    public IncidenteDTO cambiarEstado(
            Long incidenteId,
            Long serenazgoId,
            CambioEstadoRequest request) {

        IncidenteDTO incidente =
                incidentClient
                        .obtenerIncidente(incidenteId)
                        .getResponse();

        EstadoIncidente estadoAnterior =
                EstadoIncidente.valueOf(
                        incidente.getEstado()
                );

        EstadoIncidente nuevoEstado = request.getNuevoEstado();

        validarTransicion(estadoAnterior, nuevoEstado);

        incidente.setEstado(
                nuevoEstado.name()
        );

        IncidenteDTO incidenteActualizado =
                incidentClient
                        .actualizarIncidente(
                                incidenteId,
                                incidente
                        )
                        .getResponse();

        HistorialEstado historial =
                new HistorialEstado();

        historial.setIncidenteId(
                incidenteId
        );

        historial.setSerenazgoId(
                serenazgoId
        );

        historial.setEstadoAnterior(
                estadoAnterior
        );

        historial.setEstadoNuevo(
                nuevoEstado
        );

        historial.setFechaCambio(
                LocalDateTime.now()
        );

        historialEstadoRepository.save(
                historial
        );

        eventPublisherService.publicarIncidenteActualizado(
                incidenteId, serenazgoId, nuevoEstado.name());

        if (nuevoEstado == EstadoIncidente.CERRADO) {
            Patrulla patrulla = patrullaService.obtenerPatrulla(serenazgoId);
            if (patrulla != null && !asignacionIncidenteRepository
                    .findByPatrullaId(patrulla.getId()).stream()
                    .anyMatch(a -> {
                        IncidenteDTO inc = obtenerIncidente(a.getIncidenteId());
                        return inc != null
                                && !EstadoIncidente.CERRADO.name().equals(inc.getEstado())
                                && !a.getIncidenteId().equals(incidenteId);
                    })) {
                patrulla.setEstado(EstadoPatrulla.DISPONIBLE);
                patrullaService.guardarPatrulla(patrulla);
            }
            eventPublisherService.publicarIncidenteCerrado(incidenteId, serenazgoId);
        }

        return incidenteActualizado;
    }

    private void validarTransicion(EstadoIncidente estadoActual, EstadoIncidente nuevoEstado) {
        List<EstadoIncidente> permitidos = TRANSICIONES_PERMITIDAS.get(estadoActual);
        if (permitidos == null || !permitidos.contains(nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición no permitida: de " + estadoActual + " a " + nuevoEstado);
        }
    }

    public List<HistorialEstadoResponse>
    obtenerHistorial(Long incidenteId) {

        return historialEstadoRepository
                .findByIncidenteId(
                        incidenteId
                )
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private HistorialEstadoResponse convertirResponse(
            HistorialEstado historial) {

        HistorialEstadoResponse response =
                new HistorialEstadoResponse();

        response.setId(
                historial.getId()
        );

        response.setIncidenteId(
                historial.getIncidenteId()
        );

        response.setSerenazgoId(
                historial.getSerenazgoId()
        );

        response.setEstadoAnterior(
                historial.getEstadoAnterior()
        );

        response.setEstadoNuevo(
                historial.getEstadoNuevo()
        );

        response.setFechaCambio(
                historial.getFechaCambio()
        );

        return response;
    }
}
