package com.alertavecinal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.entity.Incidente;



@Repository
public interface IncidenteRepository extends JpaRepository<Incidente, Long> {

    List<Incidente> findByUsuarioId(Long usuarioId);
    List<Incidente> findByActivoTrue();
    List<Incidente> findByUsuarioIdAndActivoTrue(Long usuarioId);
}
