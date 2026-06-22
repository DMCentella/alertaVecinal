package com.alertavecinal.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alertavecinal.dto.AuthResponse;
import com.alertavecinal.dto.LoginRequest;
import com.alertavecinal.dto.RegisterRequest;
import com.alertavecinal.dto.UsuarioResponse;
import com.alertavecinal.entity.Usuario;
import com.alertavecinal.exception.ResourceNotFoundException;
import com.alertavecinal.repository.UsuarioRepository;
import com.alertavecinal.security.JwtUtils;
import com.alertavecinal.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return new AuthResponse(jwt, loginRequest.getUsername(),
                "Autenticación exitosa", userDetails.getId());
    }

    public AuthResponse registerUser(RegisterRequest signUpRequest) {
        if (usuarioRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: El nombre de usuario ya está en uso!");
        }

        Usuario user = new Usuario();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRol(signUpRequest.getRol() != null ? signUpRequest.getRol() : "ROLE_VECINO");
        user.setActivo(true);

        usuarioRepository.save(user);

        return new AuthResponse(null, user.getUsername(),
                "Usuario registrado exitosamente!", user.getId());
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::toUsuarioResponse)
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID = " + id + " no existe."));
        return toUsuarioResponse(usuario);
    }

    public UsuarioResponse cambiarRol(Long id, String nuevoRol) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID = " + id + " no existe."));
        usuario.setRol(nuevoRol);
        usuarioRepository.save(usuario);
        return toUsuarioResponse(usuario);
    }

    public UsuarioResponse bloquearUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID = " + id + " no existe."));
        usuario.setActivo(!usuario.getActivo());
        usuarioRepository.save(usuario);
        return toUsuarioResponse(usuario);
    }

    public UsuarioResponse resetPassword(Long id, String nuevaPassword) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con ID = " + id + " no existe."));
        usuario.setPassword(encoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        return toUsuarioResponse(usuario);
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    public long contarPorRol(String rol) {
        return usuarioRepository.findByRol(rol).size();
    }

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        return UsuarioResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .rol(u.getRol())
                .activo(u.getActivo())
                .build();
    }
}
