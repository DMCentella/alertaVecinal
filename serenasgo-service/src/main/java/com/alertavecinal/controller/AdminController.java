package com.alertavecinal.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alertavecinal.dto.DashboardResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.service.DashboardService;
import com.alertavecinal.service.ReportService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DashboardService dashboardService;
    private final ReportService reportService;

    @GetMapping("/dashboard")
    public ResponseEntity<GenericResponseDto<DashboardResponse>> obtenerDashboard() {
        DashboardResponse dashboard = dashboardService.obtenerDashboard();
        GenericResponseDto<DashboardResponse> body =
                GenericResponseDto.<DashboardResponse>builder()
                        .response(dashboard)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/reportes/incidentes")
    public ResponseEntity<byte[]> descargarReporteIncidentes() {
        byte[] pdf = reportService.generarReporteIncidentes();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_incidentes.pdf");
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
