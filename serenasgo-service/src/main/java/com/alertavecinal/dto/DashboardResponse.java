package com.alertavecinal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private IncidentesStats incidentes;
    private PatrullasStats patrullas;
    private UsuariosStats usuarios;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncidentesStats {
        private long total;
        private long pendientes;
        private long asignados;
        private long enProceso;
        private long atendidos;
        private long cerrados;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatrullasStats {
        private long total;
        private long disponibles;
        private long atendiendo;
        private long fueraDeServicio;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsuariosStats {
        private long total;
        private long vecinos;
        private long serenazgo;
        private long supervisores;
        private long administradores;
    }
}
