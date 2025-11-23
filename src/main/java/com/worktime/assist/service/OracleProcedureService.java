package com.worktime.assist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
public class OracleProcedureService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void iniciarPausa(LocalDateTime inicio) {
        try {
            String sql = "{ call iniciar_pausa(?) }";
            jdbcTemplate.update(sql, Timestamp.valueOf(inicio));
            log.info("procedure iniciar_pausa executada com sucesso para data: {}", inicio);
        } catch (Exception e) {
            log.warn("erro ao executar procedure iniciar_pausa: {}", e.getMessage());
            // não lança exceção para não interromper o fluxo principal
        }
    }

    @Transactional
    public void encerrarPausa(LocalDateTime fim, Long duracao) {
        try {
            String sql = "{ call encerrar_pausa(?, ?) }";
            jdbcTemplate.update(sql, Timestamp.valueOf(fim), duracao);
            log.info("procedure encerrar_pausa executada com sucesso para data: {} e duração: {}", fim, duracao);
        } catch (Exception e) {
            log.warn("erro ao executar procedure encerrar_pausa: {}", e.getMessage());
            // não lança exceção para não interromper o fluxo principal
        }
    }
}

