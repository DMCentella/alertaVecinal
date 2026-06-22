package com.alertavecinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.entity.Incidente;
import com.alertavecinal.exception.ResourceNotFoundException;
import com.alertavecinal.security.JwtUtils;
import com.alertavecinal.service.IncidenteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/incidents")
public class IncidenteController {

    private final IncidenteService incidenteService;
    private final JwtUtils jwtUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('SERENAZGO', 'ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<List<Incidente>>> listarIncidentes() {
        List<Incidente> incidentes = incidenteService.listarTodos();
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<Incidente>> response =
                GenericResponseDto.<List<Incidente>>builder()
                        .response(incidentes)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/mis-incidentes")
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<GenericResponseDto<List<Incidente>>> listarMisIncidentes() {
        Long usuarioId = getUsuarioIdFromJwt();
        List<Incidente> incidentes = incidenteService.listarPorUsuario(usuarioId);
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<Incidente>> response =
                GenericResponseDto.<List<Incidente>>builder()
                        .response(incidentes)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GenericResponseDto<Incidente>> obtenerIncidente(@PathVariable Long id) {
        Incidente incidente = incidenteService.buscarPorId(id);
        if (incidente == null) {
            throw new ResourceNotFoundException(
                    "El incidente con ID = " + id + " no existe.");
        }
        GenericResponseDto<Incidente> response =
                GenericResponseDto.<Incidente>builder()
                        .response(incidente)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<GenericResponseDto<Incidente>> crearIncidente(
            @Valid @RequestBody Incidente incidente) {
        Long usuarioId = getUsuarioIdFromJwt();
        incidente.setUsuarioId(usuarioId);
        Incidente nuevo = incidenteService.guardar(incidente);
        GenericResponseDto<Incidente> response =
                GenericResponseDto.<Incidente>builder()
                        .response(nuevo)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<Incidente>> actualizarIncidente(
            @PathVariable Long id, @RequestBody Incidente detalles) {
        Incidente actualizado = incidenteService.actualizar(id, detalles);
        if (actualizado == null) {
            throw new ResourceNotFoundException(
                    "El incidente con ID = " + id + " no existe.");
        }
        GenericResponseDto<Incidente> response =
                GenericResponseDto.<Incidente>builder()
                        .response(actualizado)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<String>> eliminarIncidente(@PathVariable Long id) {
        Incidente incidente = incidenteService.buscarPorId(id);
        if (incidente == null) {
            throw new ResourceNotFoundException(
                    "El incidente con ID = " + id + " no existe.");
        }
        incidenteService.eliminar(id);
        GenericResponseDto<String> response =
                GenericResponseDto.<String>builder()
                        .response("Incidente desactivado exitosamente")
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Long getUsuarioIdFromJwt() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        String header = attrs.getRequest().getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return jwtUtils.getUserIdFromJwtToken(header.substring(7));
        }
        return null;
    }
}
