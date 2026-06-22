package com.alertavecinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alertavecinal.dto.AsignacionRequest;
import com.alertavecinal.dto.AsignacionResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.ReasignarRequest;
import com.alertavecinal.security.JwtUtils;
import com.alertavecinal.service.AsignacionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/asignaciones")
@PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<GenericResponseDto<AsignacionResponse>> crearAsignacion(
            @Valid @RequestBody AsignacionRequest request) {
        Long supervisorId = getUsuarioIdFromJwt();
        AsignacionResponse response = asignacionService.crearAsignacion(request, supervisorId);
        GenericResponseDto<AsignacionResponse> body =
                GenericResponseDto.<AsignacionResponse>builder()
                        .response(response)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<GenericResponseDto<List<AsignacionResponse>>> listarAsignaciones() {
        List<AsignacionResponse> asignaciones = asignacionService.listarAsignaciones();
        if (asignaciones.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<AsignacionResponse>> body =
                GenericResponseDto.<List<AsignacionResponse>>builder()
                        .response(asignaciones)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponseDto<AsignacionResponse>> obtenerAsignacion(
            @PathVariable Long id) {
        AsignacionResponse response = asignacionService.obtenerAsignacion(id);
        GenericResponseDto<AsignacionResponse> body =
                GenericResponseDto.<AsignacionResponse>builder()
                        .response(response)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/{id}/reasignar")
    public ResponseEntity<GenericResponseDto<AsignacionResponse>> reasignarIncidente(
            @PathVariable Long id,
            @Valid @RequestBody ReasignarRequest request) {
        Long supervisorId = getUsuarioIdFromJwt();
        AsignacionResponse response = asignacionService.reasignarIncidente(id, request, supervisorId);
        GenericResponseDto<AsignacionResponse> body =
                GenericResponseDto.<AsignacionResponse>builder()
                        .response(response)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
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
