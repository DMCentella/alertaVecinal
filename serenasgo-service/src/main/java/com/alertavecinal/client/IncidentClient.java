package com.alertavecinal.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.IncidenteDTO;



@FeignClient(name = "INCIDENT-SERVICE")
public interface IncidentClient {

    @GetMapping("/api/incidents")
    GenericResponseDto<List<IncidenteDTO>>
    listarIncidentes();

    @GetMapping("/api/incidents/{id}")
    GenericResponseDto<IncidenteDTO>
    obtenerIncidente(
            @PathVariable Long id
    );

    @PutMapping("/api/incidents/{id}")
    GenericResponseDto<IncidenteDTO>
    actualizarIncidente(
            @PathVariable Long id,
            @RequestBody IncidenteDTO incidente
    );
}