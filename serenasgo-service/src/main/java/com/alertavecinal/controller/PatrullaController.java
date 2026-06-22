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

import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.PatrullaRequest;
import com.alertavecinal.dto.PatrullaResponse;
import com.alertavecinal.entity.Patrulla;
import com.alertavecinal.service.PatrullaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/patrullas")
public class PatrullaController {

    private final PatrullaService patrullaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<PatrullaResponse>> crearPatrulla(
            @Valid @RequestBody PatrullaRequest request) {
        PatrullaResponse response = patrullaService.crearPatrulla(request);
        GenericResponseDto<PatrullaResponse> body =
                GenericResponseDto.<PatrullaResponse>builder()
                        .response(response)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<List<Patrulla>>> listarPatrullas() {
        List<Patrulla> patrullas = patrullaService.listarPatrullas();
        if (patrullas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        GenericResponseDto<List<Patrulla>> body =
                GenericResponseDto.<List<Patrulla>>builder()
                        .response(patrullas)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<GenericResponseDto<Patrulla>> obtenerPatrulla(@PathVariable Long id) {
        Patrulla patrulla = patrullaService.obtenerPatrulla(id);
        GenericResponseDto<Patrulla> body =
                GenericResponseDto.<Patrulla>builder()
                        .response(patrulla)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<Patrulla>> actualizarPatrulla(
            @PathVariable Long id, @Valid @RequestBody PatrullaRequest request) {
        Patrulla patrulla = patrullaService.actualizarPatrulla(id, request);
        GenericResponseDto<Patrulla> body =
                GenericResponseDto.<Patrulla>builder()
                        .response(patrulla)
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<String>> eliminarPatrulla(@PathVariable Long id) {
        Patrulla patrulla = patrullaService.obtenerPatrulla(id);
        patrullaService.eliminarPatrulla(id);
        GenericResponseDto<String> body =
                GenericResponseDto.<String>builder()
                        .response("Patrulla desactivada exitosamente")
                        .build();
        return new ResponseEntity<>(body, HttpStatus.OK);
    }
}
