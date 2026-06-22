package com.alertavecinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.alertavecinal.entity.HistorialEstado;



@Repository
public interface HistorialEstadoRepository
        extends JpaRepository<HistorialEstado, Long> {

    List<HistorialEstado> findByIncidenteId(Long incidenteId);

    List<HistorialEstado> findBySerenazgoId(Long serenazgoId);

}