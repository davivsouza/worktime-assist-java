package com.worktime.assist.controller;

import com.worktime.assist.dto.PausaRequest;
import com.worktime.assist.dto.PausaResponse;
import com.worktime.assist.service.PausaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pausas")
@RequiredArgsConstructor
@Tag(name = "Pausas", description = "API para gerenciamento de pausas para ergonomia")
public class PausaController {

    private final PausaService pausaService;

    @PostMapping
    @Operation(summary = "Criar nova pausa", description = "cria uma nova pausa. se inicio não for informado, usa data/hora atual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "pausa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    public ResponseEntity<PausaResponse> criarPausa(@Valid @RequestBody PausaRequest request) {
        PausaResponse response = pausaService.criarPausa(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pausas", description = "retorna lista paginada de pausas com ordenação e filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "lista de pausas retornada com sucesso")
    })
    public ResponseEntity<Page<PausaResponse>> listarPausas(
            @Parameter(description = "paginação: page, size, sort")
            @PageableDefault(size = 10, sort = "inicio", direction = Sort.Direction.DESC) @NonNull Pageable pageable) {
        Page<PausaResponse> pausas = pausaService.listarPausas(pageable);
        return ResponseEntity.ok(pausas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pausa por ID", description = "retorna uma pausa específica pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pausa encontrada"),
            @ApiResponse(responseCode = "404", description = "pausa não encontrada")
    })
    public ResponseEntity<PausaResponse> buscarPorId(
            @Parameter(description = "ID da pausa") @PathVariable @NonNull UUID id) {
        PausaResponse response = pausaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar pausa", description = "atualiza uma pausa existente. ao informar fim, calcula duração automaticamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "pausa atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "pausa não encontrada"),
            @ApiResponse(responseCode = "400", description = "dados inválidos")
    })
    public ResponseEntity<PausaResponse> atualizarPausa(
            @Parameter(description = "ID da pausa") @PathVariable @NonNull UUID id,
            @Valid @RequestBody PausaRequest request) {
        PausaResponse response = pausaService.atualizarPausa(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar pausa", description = "remove uma pausa do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "pausa deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "pausa não encontrada")
    })
    public ResponseEntity<Void> deletarPausa(
            @Parameter(description = "ID da pausa") @PathVariable @NonNull UUID id) {
        pausaService.deletarPausa(id);
        return ResponseEntity.noContent().build();
    }
}

