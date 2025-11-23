package com.worktime.assist.controller;

import com.worktime.assist.service.OracleProcedureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/procedures")
@RequiredArgsConstructor
@Tag(name = "Procedures e Funções", description = "endpoints para chamar procedures e funções do banco de dados")
public class ProcedureController {

    private final OracleProcedureService procedureService;

    @PostMapping("/inserir-usuario")
    @Operation(summary = "Inserir usuário via procedure", description = "chama a procedure inserir_usuario do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuário inserido via procedure com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    public ResponseEntity<Map<String, Object>> inserirUsuarioViaProcedure(
            @Valid @RequestBody InserirUsuarioRequest request) {
        UUID id = procedureService.inserirUsuario(
                request.getNome(),
                request.getEmail(),
                request.getSenha()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("mensagem", "usuário inserido via procedure inserir_usuario");
        response.put("nome", request.getNome());
        response.put("email", request.getEmail());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/inserir-pausa")
    @Operation(summary = "Inserir pausa via procedure", description = "chama a procedure inserir_pausa do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "pausa inserida via procedure com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    public ResponseEntity<Map<String, Object>> inserirPausaViaProcedure(
            @Valid @RequestBody InserirPausaRequest request) {
        UUID id = procedureService.inserirPausa(
                request.getInicio(),
                request.getFim(),
                request.getUsuarioId()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        response.put("mensagem", "pausa inserida via procedure inserir_pausa");
        response.put("inicio", request.getInicio());
        response.put("fim", request.getFim());
        response.put("usuarioId", request.getUsuarioId());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/calcular-duracao")
    @Operation(summary = "Calcular duração via função", description = "chama a função calcular_duracao_pausa do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "duração calculada com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    public ResponseEntity<Map<String, Object>> calcularDuracaoViaFuncao(
            @Valid @RequestBody CalcularDuracaoRequest request) {
        Long duracao = procedureService.calcularDuracaoPausa(
                request.getInicio(),
                request.getFim()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("duracaoSegundos", duracao);
        response.put("duracaoMinutos", duracao / 60.0);
        response.put("duracaoHoras", duracao / 3600.0);
        response.put("mensagem", "duração calculada via função calcular_duracao_pausa");
        response.put("inicio", request.getInicio());
        response.put("fim", request.getFim());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/validar-email")
    @Operation(summary = "Validar email via função", description = "chama a função validar_email_usuario do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "validação realizada com sucesso")
    })
    public ResponseEntity<Map<String, Object>> validarEmailViaFuncao(
            @Parameter(description = "email a ser validado")
            @RequestParam @Email @NonNull String email) {
        Boolean valido = procedureService.validarEmailUsuario(email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("email", email);
        response.put("valido", valido);
        response.put("mensagem", valido 
                ? "email válido e disponível (via função validar_email_usuario)"
                : "email inválido ou já cadastrado (via função validar_email_usuario)");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contar-pausas/{usuarioId}")
    @Operation(summary = "Contar pausas via função", description = "chama a função contar_pausas_usuario do banco de dados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "contagem realizada com sucesso")
    })
    public ResponseEntity<Map<String, Object>> contarPausasViaFuncao(
            @Parameter(description = "ID do usuário")
            @PathVariable @NonNull UUID usuarioId,
            @Parameter(description = "data inicial (opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @Parameter(description = "data final (opcional)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        
        Long total;
        if (dataInicio != null && dataFim != null) {
            total = procedureService.contarPausasUsuarioPorPeriodo(usuarioId, dataInicio, dataFim);
        } else {
            total = procedureService.contarPausasUsuario(usuarioId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuarioId);
        response.put("totalPausas", total);
        response.put("mensagem", "contagem realizada via função contar_pausas_usuario");
        if (dataInicio != null) {
            response.put("dataInicio", dataInicio);
        }
        if (dataFim != null) {
            response.put("dataFim", dataFim);
        }
        
        return ResponseEntity.ok(response);
    }

    @Data
    public static class InserirUsuarioRequest {
        @NotNull(message = "nome é obrigatório")
        private String nome;
        
        @NotNull(message = "email é obrigatório")
        @Email(message = "email inválido")
        private String email;
        
        @NotNull(message = "senha é obrigatória")
        private String senha;
    }

    @Data
    public static class InserirPausaRequest {
        @NotNull(message = "inicio é obrigatório")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime inicio;
        
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime fim;
        
        @NotNull(message = "usuarioId é obrigatório")
        private UUID usuarioId;
    }

    @Data
    public static class CalcularDuracaoRequest {
        @NotNull(message = "inicio é obrigatório")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime inicio;
        
        @NotNull(message = "fim é obrigatório")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime fim;
    }
}

