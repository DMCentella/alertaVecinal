package com.alertavecinal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alertavecinal.dto.CambioEstadoRequest;
import com.alertavecinal.dto.ComentarioRequest;
import com.alertavecinal.dto.ComentarioResponse;
import com.alertavecinal.dto.EvidenciaResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.HistorialEstadoResponse;
import com.alertavecinal.dto.IncidenteDTO;
import com.alertavecinal.exception.ResourceNotFoundException;
import com.alertavecinal.security.JwtUtils;
import com.alertavecinal.service.ComentarioService;
import com.alertavecinal.service.EvidenciaService;
import com.alertavecinal.service.SerenazgoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/serenazgo")
public class SerenazgoController {

    private final SerenazgoService serenazgoService;
    private final JwtUtils jwtUtils;
    private final ComentarioService comentarioService;
    private final EvidenciaService evidenciaService;

    @GetMapping("/incidentes")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> listarIncidentes() {
        List<IncidenteDTO> incidentes =
                serenazgoService.listarIncidentes();
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<IncidenteDTO>> response =
                GenericResponseDto.<List<IncidenteDTO>>builder()
                        .response(incidentes)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/mis-incidentes")
    @PreAuthorize("hasRole('SERENAZGO')")
    public ResponseEntity<GenericResponseDto<List<IncidenteDTO>>> listarMisIncidentes() {
        Long usuarioId = getUsuarioIdFromJwt();
        List<IncidenteDTO> incidentes = serenazgoService.listarMisIncidentes(usuarioId);
        if (incidentes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<IncidenteDTO>> response =
                GenericResponseDto.<List<IncidenteDTO>>builder()
                        .response(incidentes)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/incidentes/{id}")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<IncidenteDTO>>
    obtenerIncidente(@PathVariable Long id) {
        IncidenteDTO incidente =
                serenazgoService.obtenerIncidente(id);
        if (incidente == null) {
            throw new ResourceNotFoundException(
                    "El incidente con ID = "
                            + id +
                            " no existe.");
        }
        GenericResponseDto<IncidenteDTO> response =
                GenericResponseDto.<IncidenteDTO>builder()
                        .response(incidente)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/incidentes/{id}/estado")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<GenericResponseDto<IncidenteDTO>>
    cambiarEstado(
            @PathVariable Long id,
            @RequestParam Long serenazgoId,
            @Valid @RequestBody CambioEstadoRequest request) {
        IncidenteDTO actualizado =
                serenazgoService.cambiarEstado(
                        id,
                        serenazgoId,
                        request
                );
        GenericResponseDto<IncidenteDTO> response =
                GenericResponseDto.<IncidenteDTO>builder()
                        .response(actualizado)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/incidentes/{id}/historial")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<List<HistorialEstadoResponse>>>
    obtenerHistorial(@PathVariable Long id) {
        List<HistorialEstadoResponse> historial =
                serenazgoService.obtenerHistorial(id);
        GenericResponseDto<List<HistorialEstadoResponse>> response =
                GenericResponseDto
                        .<List<HistorialEstadoResponse>>builder()
                        .response(historial)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/incidentes/{id}/comentarios")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<GenericResponseDto<ComentarioResponse>> crearComentario(
            @PathVariable Long id,
            @RequestBody @Valid ComentarioRequest request) {
        Long usuarioId = getUsuarioIdFromJwt();
        ComentarioResponse comentario = comentarioService.crearComentario(id, usuarioId, request);
        GenericResponseDto<ComentarioResponse> body =
                GenericResponseDto.<ComentarioResponse>builder()
                        .response(comentario)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping("/incidentes/{id}/comentarios")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<GenericResponseDto<List<ComentarioResponse>>> listarComentarios(
            @PathVariable Long id) {
        List<ComentarioResponse> comentarios = comentarioService.listarComentarios(id);
        GenericResponseDto<List<ComentarioResponse>> body =
                GenericResponseDto.<List<ComentarioResponse>>builder()
                        .response(comentarios)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PostMapping("/incidentes/{id}/evidencias")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<GenericResponseDto<EvidenciaResponse>> subirEvidencia(
            @PathVariable Long id,
            @RequestPart("archivo") MultipartFile archivo) {
        try {
            EvidenciaResponse evidencia = evidenciaService.subirEvidencia(id, archivo);
            GenericResponseDto<EvidenciaResponse> body =
                    GenericResponseDto.<EvidenciaResponse>builder()
                            .response(evidencia)
                            .build();
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException("Error al subir evidencia: " + e.getMessage(), e);
        }
    }

    @GetMapping("/incidentes/{id}/evidencias")
    @PreAuthorize("hasAnyRole('SERENAZGO', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<GenericResponseDto<List<EvidenciaResponse>>> listarEvidencias(
            @PathVariable Long id) {
        List<EvidenciaResponse> evidencias = evidenciaService.listarEvidencias(id);
        GenericResponseDto<List<EvidenciaResponse>> body =
                GenericResponseDto.<List<EvidenciaResponse>>builder()
                        .response(evidencias)
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
