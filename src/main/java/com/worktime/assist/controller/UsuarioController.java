package com.worktime.assist.controller;

import com.worktime.assist.dto.UsuarioRequest;
import com.worktime.assist.dto.UsuarioResponse;
import com.worktime.assist.entity.Usuario;
import com.worktime.assist.mapper.UsuarioMapper;
import com.worktime.assist.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "API para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @Operation(summary = "Criar usuário", description = "cria um novo usuário. email deve ser único")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "dados inválidos"),
            @ApiResponse(responseCode = "409", description = "email já cadastrado")
    })
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Meu perfil", description = "retorna os dados do usuário logado (baseado no token JWT)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "dados do usuário"),
            @ApiResponse(responseCode = "401", description = "não autenticado")
    })
    public ResponseEntity<UsuarioResponse> meuPerfil() {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        UsuarioResponse response = usuarioMapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "retorna um usuário específico pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponse> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable @NonNull UUID id) {
        UsuarioResponse response = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }
}

