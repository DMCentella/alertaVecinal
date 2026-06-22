package com.alertavecinal.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alertavecinal.dto.ComentarioRequest;
import com.alertavecinal.dto.ComentarioResponse;
import com.alertavecinal.entity.ComentarioIncidente;
import com.alertavecinal.repository.ComentarioIncidenteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioIncidenteRepository comentarioRepository;

    public ComentarioResponse crearComentario(Long incidenteId, Long usuarioId, ComentarioRequest request) {
        ComentarioIncidente comentario = new ComentarioIncidente();
        comentario.setIncidenteId(incidenteId);
        comentario.setUsuarioId(usuarioId);
        comentario.setComentario(request.getComentario());
        comentario.setFecha(LocalDateTime.now());
        comentario = comentarioRepository.save(comentario);

        return ComentarioResponse.builder()
                .id(comentario.getId())
                .incidenteId(comentario.getIncidenteId())
                .usuarioId(comentario.getUsuarioId())
                .comentario(comentario.getComentario())
                .fecha(comentario.getFecha())
                .build();
    }

    public List<ComentarioResponse> listarComentarios(Long incidenteId) {
        return comentarioRepository.findByIncidenteIdOrderByFechaDesc(incidenteId).stream()
                .map(c -> ComentarioResponse.builder()
                        .id(c.getId())
                        .incidenteId(c.getIncidenteId())
                        .usuarioId(c.getUsuarioId())
                        .comentario(c.getComentario())
                        .fecha(c.getFecha())
                        .build())
                .collect(Collectors.toList());
    }
}
