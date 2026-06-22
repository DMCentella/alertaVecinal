package com.alertavecinal.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alertavecinal.dto.EvidenciaResponse;
import com.alertavecinal.entity.Evidencia;
import com.alertavecinal.repository.EvidenciaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvidenciaService {

    private final EvidenciaRepository evidenciaRepository;

    private static final String UPLOAD_DIR = "uploads/evidencias";

    public EvidenciaResponse subirEvidencia(Long incidenteId, MultipartFile archivo) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String nombreArchivo = UUID.randomUUID() + "_" + archivo.getOriginalFilename();
        Path filePath = uploadPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Evidencia evidencia = new Evidencia();
        evidencia.setIncidenteId(incidenteId);
        evidencia.setNombreArchivo(archivo.getOriginalFilename());
        evidencia.setRutaArchivo(filePath.toString());
        evidencia.setFechaSubida(LocalDateTime.now());
        evidencia = evidenciaRepository.save(evidencia);

        return EvidenciaResponse.builder()
                .id(evidencia.getId())
                .incidenteId(evidencia.getIncidenteId())
                .nombreArchivo(evidencia.getNombreArchivo())
                .rutaArchivo(evidencia.getRutaArchivo())
                .fechaSubida(evidencia.getFechaSubida())
                .build();
    }

    public List<EvidenciaResponse> listarEvidencias(Long incidenteId) {
        return evidenciaRepository.findByIncidenteId(incidenteId).stream()
                .map(e -> EvidenciaResponse.builder()
                        .id(e.getId())
                        .incidenteId(e.getIncidenteId())
                        .nombreArchivo(e.getNombreArchivo())
                        .rutaArchivo(e.getRutaArchivo())
                        .fechaSubida(e.getFechaSubida())
                        .build())
                .collect(Collectors.toList());
    }
}
