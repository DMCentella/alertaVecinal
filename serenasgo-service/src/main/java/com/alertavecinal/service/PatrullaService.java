package com.alertavecinal.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alertavecinal.client.AuthClient;
import com.alertavecinal.dto.PatrullaRequest;
import com.alertavecinal.dto.PatrullaResponse;
import com.alertavecinal.entity.Patrulla;
import com.alertavecinal.enums.EstadoPatrulla;
import com.alertavecinal.exception.ResourceNotFoundException;
import com.alertavecinal.repository.PatrullaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatrullaService {

    private final PatrullaRepository patrullaRepository;
    private final AuthClient authClient;

    public PatrullaResponse crearPatrulla(PatrullaRequest request) {
        if (patrullaRepository.existsByCodigo(request.getCodigo())) {
            throw new IllegalArgumentException(
                    "Ya existe una patrulla con el código: " + request.getCodigo());
        }

        String username = "patrulla_" + request.getCodigo().toLowerCase();
        String password = generarPassword();

        Map<String, String> authRequest = new HashMap<>();
        authRequest.put("username", username);
        authRequest.put("password", password);
        authRequest.put("rol", "ROLE_SERENAZGO");

        var authResponse = authClient.registerUser(authRequest);
        if (authResponse == null || authResponse.getResponse() == null || authResponse.getResponse().getUserId() == null) {
            throw new RuntimeException("Error al crear el usuario para la patrulla en auth-service");
        }

        Long usuarioId = authResponse.getResponse().getUserId();

        Patrulla patrulla = new Patrulla();
        patrulla.setCodigo(request.getCodigo());
        patrulla.setNombre(request.getNombre());
        patrulla.setSector(request.getSector());
        patrulla.setUsuarioId(usuarioId);
        patrulla.setEstado(EstadoPatrulla.DISPONIBLE);

        patrulla = patrullaRepository.save(patrulla);

        return PatrullaResponse.builder()
                .id(patrulla.getId())
                .codigo(patrulla.getCodigo())
                .nombre(patrulla.getNombre())
                .sector(patrulla.getSector())
                .usuarioId(patrulla.getUsuarioId())
                .estado(patrulla.getEstado())
                .username(username)
                .password(password)
                .build();
    }

    public List<Patrulla> listarPatrullas() {
        return patrullaRepository.findByActivoTrue();
    }

    public Patrulla obtenerPatrulla(Long id) {
        return patrullaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La patrulla con ID = " + id + " no existe."));
    }

    public Patrulla obtenerPatrullaPorUsuario(Long usuarioId) {
        return patrullaRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró patrulla para el usuario con ID = " + usuarioId));
    }

    public Patrulla actualizarPatrulla(Long id, PatrullaRequest request) {
        Patrulla patrulla = obtenerPatrulla(id);
        patrulla.setCodigo(request.getCodigo());
        patrulla.setNombre(request.getNombre());
        patrulla.setSector(request.getSector());
        return patrullaRepository.save(patrulla);
    }

    public void eliminarPatrulla(Long id) {
        Patrulla patrulla = obtenerPatrulla(id);
        patrulla.setActivo(false);
        patrullaRepository.save(patrulla);
    }

    public Patrulla guardarPatrulla(Patrulla patrulla) {
        return patrullaRepository.save(patrulla);
    }

    private String generarPassword() {
        byte[] bytes = new byte[12];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
