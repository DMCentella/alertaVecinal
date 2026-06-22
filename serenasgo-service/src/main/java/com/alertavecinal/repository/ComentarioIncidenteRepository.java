package com.alertavecinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.entity.ComentarioIncidente;

@Repository
public interface ComentarioIncidenteRepository extends JpaRepository<ComentarioIncidente, Long> {

    List<ComentarioIncidente> findByIncidenteIdOrderByFechaDesc(Long incidenteId);
}
