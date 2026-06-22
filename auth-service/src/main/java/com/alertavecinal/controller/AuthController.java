package com.alertavecinal.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alertavecinal.dto.AuthResponse;
import com.alertavecinal.dto.CambiarRolRequest;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.LoginRequest;
import com.alertavecinal.dto.RegisterRequest;
import com.alertavecinal.dto.ResetPasswordRequest;
import com.alertavecinal.dto.UsuarioResponse;
import com.alertavecinal.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponseDto<AuthResponse>> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse result = authService.authenticateUser(loginRequest);
        GenericResponseDto<AuthResponse> response = GenericResponseDto.<AuthResponse>builder()
                .response(result)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponseDto<AuthResponse>> registerUser(
            @Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            AuthResponse result = authService.registerUser(signUpRequest);
            GenericResponseDto<AuthResponse> response = GenericResponseDto.<AuthResponse>builder()
                    .response(result)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            AuthResponse errorResult =
                    new AuthResponse(
                            null,
                            null,
                            e.getMessage(),
                            null
                    );
            GenericResponseDto<AuthResponse> response = GenericResponseDto.<AuthResponse>builder()
                    .response(errorResult)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/usuarios")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<List<UsuarioResponse>>> listarUsuarios() {
        List<UsuarioResponse> usuarios = authService.listarUsuarios();
        GenericResponseDto<List<UsuarioResponse>> response =
                GenericResponseDto.<List<UsuarioResponse>>builder()
                        .response(usuarios)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/usuarios/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<UsuarioResponse>> obtenerUsuario(@PathVariable Long id) {
        UsuarioResponse usuario = authService.obtenerUsuario(id);
        GenericResponseDto<UsuarioResponse> response =
                GenericResponseDto.<UsuarioResponse>builder()
                        .response(usuario)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<UsuarioResponse>> cambiarRol(
            @PathVariable Long id, @Valid @RequestBody CambiarRolRequest request) {
        UsuarioResponse usuario = authService.cambiarRol(id, request.getRol());
        GenericResponseDto<UsuarioResponse> response =
                GenericResponseDto.<UsuarioResponse>builder()
                        .response(usuario)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/usuarios/{id}/bloquear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<UsuarioResponse>> bloquearUsuario(@PathVariable Long id) {
        UsuarioResponse usuario = authService.bloquearUsuario(id);
        GenericResponseDto<UsuarioResponse> response =
                GenericResponseDto.<UsuarioResponse>builder()
                        .response(usuario)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/usuarios/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<UsuarioResponse>> resetPassword(
            @PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        UsuarioResponse usuario = authService.resetPassword(id, request.getNuevaPassword());
        GenericResponseDto<UsuarioResponse> response =
                GenericResponseDto.<UsuarioResponse>builder()
                        .response(usuario)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/supervisores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<UsuarioResponse>> crearSupervisor(
            @Valid @RequestBody RegisterRequest request) {
        request.setRol("ROLE_SUPERVISOR");
        AuthResponse result = authService.registerUser(request);
        UsuarioResponse usuarioResponse = UsuarioResponse.builder()
                .id(result.getUserId())
                .username(result.getUsername())
                .rol("ROLE_SUPERVISOR")
                .activo(true)
                .build();
        GenericResponseDto<UsuarioResponse> response =
                GenericResponseDto.<UsuarioResponse>builder()
                        .response(usuarioResponse)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/supervisores")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponseDto<List<UsuarioResponse>>> listarSupervisores() {
        List<UsuarioResponse> supervisores = authService.listarUsuarios().stream()
                .filter(u -> "ROLE_SUPERVISOR".equals(u.getRol()))
                .collect(Collectors.toList());
        GenericResponseDto<List<UsuarioResponse>> response =
                GenericResponseDto.<List<UsuarioResponse>>builder()
                        .response(supervisores)
                        .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
