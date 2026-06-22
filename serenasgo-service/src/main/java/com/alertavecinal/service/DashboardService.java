package com.alertavecinal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alertavecinal.client.AuthClient;
import com.alertavecinal.client.IncidentClient;
import com.alertavecinal.dto.DashboardResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.IncidenteDTO;
import com.alertavecinal.dto.UsuarioResponse;
import com.alertavecinal.enums.EstadoIncidente;
import com.alertavecinal.enums.EstadoPatrulla;
import com.alertavecinal.repository.PatrullaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncidentClient incidentClient;
    private final AuthClient authClient;
    private final PatrullaRepository patrullaRepository;

    public DashboardResponse obtenerDashboard() {
        List<IncidenteDTO> incidentes = obtenerTodosLosIncidentes();

        DashboardResponse.IncidentesStats incidentesStats = DashboardResponse.IncidentesStats.builder()
                .total(incidentes.size())
                .pendientes(contarPorEstado(incidentes, EstadoIncidente.PENDIENTE))
                .asignados(contarPorEstado(incidentes, EstadoIncidente.ASIGNADO))
                .enProceso(contarPorEstado(incidentes, EstadoIncidente.EN_PROCESO))
                .atendidos(contarPorEstado(incidentes, EstadoIncidente.ATENDIDO))
                .cerrados(contarPorEstado(incidentes, EstadoIncidente.CERRADO))
                .build();

        DashboardResponse.PatrullasStats patrullasStats = DashboardResponse.PatrullasStats.builder()
                .total(patrullaRepository.countByActivoTrue())
                .disponibles(patrullaRepository.countByActivoTrueAndEstado(EstadoPatrulla.DISPONIBLE))
                .atendiendo(patrullaRepository.countByActivoTrueAndEstado(EstadoPatrulla.ATENDIENDO))
                .fueraDeServicio(patrullaRepository.countByActivoTrueAndEstado(EstadoPatrulla.FUERA_DE_SERVICIO))
                .build();

        List<UsuarioResponse> usuarios = listarUsuarios();
        DashboardResponse.UsuariosStats usuariosStats = DashboardResponse.UsuariosStats.builder()
                .total(usuarios.size())
                .vecinos(usuarios.stream().filter(u -> "ROLE_VECINO".equals(u.getRol())).count())
                .serenazgo(usuarios.stream().filter(u -> "ROLE_SERENAZGO".equals(u.getRol())).count())
                .supervisores(usuarios.stream().filter(u -> "ROLE_SUPERVISOR".equals(u.getRol())).count())
                .administradores(usuarios.stream().filter(u -> "ROLE_ADMIN".equals(u.getRol())).count())
                .build();

        return DashboardResponse.builder()
                .incidentes(incidentesStats)
                .patrullas(patrullasStats)
                .usuarios(usuariosStats)
                .build();
    }

    public List<IncidenteDTO> obtenerTodosLosIncidentes() {
        GenericResponseDto<List<IncidenteDTO>> response = incidentClient.listarIncidentes();
        return response != null ? response.getResponse() : List.of();
    }

    private List<UsuarioResponse> listarUsuarios() {
        GenericResponseDto<List<UsuarioResponse>> response = authClient.listarUsuarios();
        return response != null ? response.getResponse() : List.of();
    }

    private long contarPorEstado(List<IncidenteDTO> incidentes, EstadoIncidente estado) {
        return incidentes.stream()
                .filter(i -> estado.name().equals(i.getEstado()))
                .count();
    }
}
