package com.worktime.assist.repository;

import com.worktime.assist.entity.Pausa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PausaRepository extends JpaRepository<Pausa, UUID> {

    @Query("SELECT p FROM Pausa p WHERE p.usuario.id = :usuarioId")
    Page<Pausa> findByUsuarioId(@Param("usuarioId") UUID usuarioId, @NonNull Pageable pageable);

    @Query("SELECT p FROM Pausa p WHERE p.usuario.id = :usuarioId AND p.inicio >= :dataInicio AND p.inicio <= :dataFim")
    Page<Pausa> findByUsuarioIdAndPeriodo(@Param("usuarioId") UUID usuarioId,
                                           @Param("dataInicio") LocalDateTime dataInicio,
                                           @Param("dataFim") LocalDateTime dataFim,
                                           @NonNull Pageable pageable);
}

