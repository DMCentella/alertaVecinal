package com.alertavecinal.client;

import com.alertavecinal.dto.AuthClientResponse;
import com.alertavecinal.dto.GenericResponseDto;
import com.alertavecinal.dto.UsuarioResponse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @PostMapping("/api/auth/register")
    AuthClientResponse registerUser(@RequestBody Map<String, String> request);

    @GetMapping("/api/auth/usuarios")
    GenericResponseDto<List<UsuarioResponse>> listarUsuarios();
}
