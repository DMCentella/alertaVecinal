package com.alertavecinal.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alertavecinal.entity.Incidente;
import com.alertavecinal.enums.EstadoIncidente;
import com.alertavecinal.repository.IncidenteRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class IncidenteService {

    private final IncidenteRepository incidenteRepository;

    public List<Incidente> listarTodos() {
        return incidenteRepository.findByActivoTrue();
    }

    public List<Incidente> listarPorUsuario(Long usuarioId) {
        return incidenteRepository.findByUsuarioIdAndActivoTrue(usuarioId);
    }

    public Incidente buscarPorId(Long id) {
        return incidenteRepository.findById(id)
                .filter(i -> Boolean.TRUE.equals(i.getActivo()))
                .orElse(null);
    }

    public Incidente guardar(Incidente incidente) {
        if (incidente.getEstado() == null) {
            incidente.setEstado(EstadoIncidente.PENDIENTE);
        }
        if (incidente.getActivo() == null) {
            incidente.setActivo(true);
        }
        return incidenteRepository.save(incidente);
    }

    public Incidente actualizar(Long id, Incidente incidenteDetalles) {
        Incidente incidente = incidenteRepository.findById(id)
                .filter(i -> Boolean.TRUE.equals(i.getActivo()))
                .orElse(null);
        if (incidente == null) return null;

        incidente.setTipo(incidenteDetalles.getTipo());
        incidente.setDescripcion(incidenteDetalles.getDescripcion());
        incidente.setDireccion(incidenteDetalles.getDireccion());
        incidente.setEstado(incidenteDetalles.getEstado());

        return incidenteRepository.save(incidente);
    }

    public void eliminar(Long id) {
        Incidente incidente = incidenteRepository.findById(id)
                .orElse(null);
        if (incidente != null && Boolean.TRUE.equals(incidente.getActivo()))  {
            incidente.setActivo(false);
            incidenteRepository.save(incidente);
        }
    }
}
