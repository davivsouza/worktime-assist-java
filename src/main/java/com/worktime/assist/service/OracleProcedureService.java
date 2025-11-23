package com.worktime.assist.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OracleProcedureService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public UUID inserirUsuario(String nome, String email, String senha) {
        return jdbcTemplate.execute((Connection connection) -> {
            try (CallableStatement cs = connection.prepareCall(
                    "{ call inserir_usuario(?, ?, ?, ?, ?) }")) {
                
                cs.registerOutParameter(1, Types.BINARY);
                cs.setString(2, nome);
                cs.setString(3, email);
                cs.setString(4, senha);
                cs.registerOutParameter(5, Types.VARCHAR);
                
                cs.execute();
                
                byte[] idBytes = cs.getBytes(1);
                String resultado = cs.getString(5);
                
                log.info("procedure inserir_usuario executada: {}", resultado);
                
                return convertRawToUuid(idBytes);
            } catch (Exception e) {
                log.error("erro ao executar procedure inserir_usuario: {}", e.getMessage(), e);
                throw new RuntimeException("erro ao inserir usuário via procedure: " + e.getMessage(), e);
            }
        });
    }

    @Transactional
    public UUID inserirPausa(LocalDateTime inicio, LocalDateTime fim, UUID usuarioId) {
        return jdbcTemplate.execute((Connection connection) -> {
            try (CallableStatement cs = connection.prepareCall(
                    "{ call inserir_pausa(?, ?, ?, ?, ?) }")) {
                
                byte[] usuarioIdBytes = convertUuidToRaw(usuarioId);
                Timestamp inicioTs = Timestamp.valueOf(inicio);
                Timestamp fimTs = fim != null ? Timestamp.valueOf(fim) : null;
                
                cs.registerOutParameter(1, Types.BINARY);
                cs.setTimestamp(2, inicioTs);
                cs.setBytes(3, usuarioIdBytes);
                if (fimTs != null) {
                    cs.setTimestamp(4, fimTs);
                } else {
                    cs.setNull(4, Types.TIMESTAMP);
                }
                cs.registerOutParameter(5, Types.VARCHAR);
                
                cs.execute();
                
                byte[] idBytes = cs.getBytes(1);
                String resultado = cs.getString(5);
                
                log.info("procedure inserir_pausa executada: {}", resultado);
                
                return convertRawToUuid(idBytes);
            } catch (Exception e) {
                log.error("erro ao executar procedure inserir_pausa: {}", e.getMessage(), e);
                throw new RuntimeException("erro ao inserir pausa via procedure: " + e.getMessage(), e);
            }
        });
    }

    public Long calcularDuracaoPausa(LocalDateTime inicio, LocalDateTime fim) {
        try {
            String sql = "SELECT calcular_duracao_pausa(?, ?) FROM DUAL";
            Long duracao = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                Timestamp.valueOf(inicio),
                Timestamp.valueOf(fim)
            );
            log.info("função calcular_duracao_pausa retornou: {} segundos", duracao);
            return duracao;
        } catch (Exception e) {
            log.error("erro ao executar função calcular_duracao_pausa: {}", e.getMessage(), e);
            throw new RuntimeException("erro ao calcular duração: " + e.getMessage(), e);
        }
    }

    public Boolean validarEmailUsuario(String email) {
        try {
            String sql = "SELECT validar_email_usuario(?) FROM DUAL";
            Integer resultado = jdbcTemplate.queryForObject(sql, Integer.class, email);
            Boolean valido = resultado != null && resultado == 1;
            log.info("função validar_email_usuario para {} retornou: {}", email, valido);
            return valido;
        } catch (Exception e) {
            log.error("erro ao executar função validar_email_usuario: {}", e.getMessage(), e);
            return false;
        }
    }

    public Long contarPausasUsuario(UUID usuarioId) {
        try {
            String sql = "SELECT contar_pausas_usuario(?) FROM DUAL";
            byte[] usuarioIdBytes = convertUuidToRaw(usuarioId);
            Long total = jdbcTemplate.queryForObject(sql, Long.class, usuarioIdBytes);
            log.info("função contar_pausas_usuario para {} retornou: {}", usuarioId, total);
            return total != null ? total : 0L;
        } catch (Exception e) {
            log.error("erro ao executar função contar_pausas_usuario: {}", e.getMessage(), e);
            return 0L;
        }
    }

    public Long contarPausasUsuarioPorPeriodo(UUID usuarioId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        try {
            String sql = "SELECT contar_pausas_usuario(?, ?, ?) FROM DUAL";
            byte[] usuarioIdBytes = convertUuidToRaw(usuarioId);
            Long total = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                usuarioIdBytes,
                Timestamp.valueOf(dataInicio),
                Timestamp.valueOf(dataFim)
            );
            log.info("função contar_pausas_usuario com período retornou: {}", total);
            return total != null ? total : 0L;
        } catch (Exception e) {
            log.error("erro ao executar função contar_pausas_usuario com período: {}", e.getMessage(), e);
            return 0L;
        }
    }

    private byte[] convertUuidToRaw(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> (8 * (7 - i)));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> (8 * (7 - i)));
        }
        
        return buffer;
    }

    private UUID convertRawToUuid(byte[] raw) {
        if (raw == null || raw.length != 16) {
            return UUID.randomUUID();
        }
        
        long msb = 0;
        long lsb = 0;
        
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (raw[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (raw[i] & 0xff);
        }
        
        return new UUID(msb, lsb);
    }
}
