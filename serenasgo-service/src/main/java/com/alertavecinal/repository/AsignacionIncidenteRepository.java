package com.alertavecinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.entity.AsignacionIncidente;

@Repository
public interface AsignacionIncidenteRepository extends JpaRepository<AsignacionIncidente, Long> {

    List<AsignacionIncidente> findByPatrullaId(Long patrullaId);

    List<AsignacionIncidente> findByIncidenteId(Long incidenteId);

    boolean existsByIncidenteId(Long incidenteId);

    boolean existsByPatrullaId(Long patrullaId);
}
