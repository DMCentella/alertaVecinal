package com.alertavecinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.entity.Evidencia;

@Repository
public interface EvidenciaRepository extends JpaRepository<Evidencia, Long> {

    List<Evidencia> findByIncidenteId(Long incidenteId);
}
