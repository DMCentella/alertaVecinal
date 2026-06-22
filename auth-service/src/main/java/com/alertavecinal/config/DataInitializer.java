package com.alertavecinal.config;

import com.alertavecinal.entity.Usuario;
import com.alertavecinal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initAdmin() {
        return args -> {

            if (!usuarioRepository.existsByUsername("admin")) {

                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRol("ROLE_ADMIN");
                admin.setActivo(true);

                usuarioRepository.save(admin);

                System.out.println("ADMIN CREADO");
            }
        };
    }
}