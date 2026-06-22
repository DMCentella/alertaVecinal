package com.alertavecinal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alertavecinal.entity.Patrulla;
import com.alertavecinal.enums.EstadoPatrulla;

@Repository
public interface PatrullaRepository extends JpaRepository<Patrulla, Long> {

    Optional<Patrulla> findByUsuarioId(Long usuarioId);

    Optional<Patrulla> findByCodigo(String codigo);

    List<Patrulla> findByEstado(EstadoPatrulla estado);

    boolean existsByCodigo(String codigo);

    List<Patrulla> findByActivoTrue();

    List<Patrulla> findByActivoTrueAndEstado(EstadoPatrulla estado);

    long countByActivoTrue();

    long countByActivoTrueAndEstado(EstadoPatrulla estado);
}
