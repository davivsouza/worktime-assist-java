package com.worktime.assist.service;

import com.worktime.assist.dto.PausaRequest;
import com.worktime.assist.dto.PausaResponse;
import com.worktime.assist.entity.Pausa;
import com.worktime.assist.entity.Usuario;
import com.worktime.assist.exception.ResourceNotFoundException;
import com.worktime.assist.mapper.PausaMapper;
import com.worktime.assist.repository.PausaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PausaService {

    private final PausaRepository pausaRepository;
    private final PausaMapper pausaMapper;
    private final OracleProcedureService oracleProcedureService;
    private final UsuarioService usuarioService;

    @Transactional
    public PausaResponse criarPausa(PausaRequest request) {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        
        Pausa pausa = pausaMapper.toEntity(request);
        pausa.setUsuario(usuario);
        
        if (pausa.getInicio() == null) {
            pausa.setInicio(LocalDateTime.now());
        }
        
        // chama procedure oracle para iniciar pausa
        oracleProcedureService.iniciarPausa(pausa.getInicio());
        
        pausa = pausaRepository.save(pausa);
        return pausaMapper.toResponse(pausa);
    }

    @Transactional(readOnly = true)
    public Page<PausaResponse> listarPausas(@NonNull Pageable pageable) {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        
        return pausaRepository.findByUsuarioId(usuario.getId(), pageable)
                .map(pausaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PausaResponse buscarPorId(@NonNull UUID id) {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        
        Pausa pausa = pausaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pausa não encontrada com id: " + id));
        
        // verifica se a pausa pertence ao usuário logado
        if (!pausa.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("pausa não encontrada com id: " + id);
        }
        
        return pausaMapper.toResponse(pausa);
    }

    @Transactional
    public PausaResponse atualizarPausa(@NonNull UUID id, PausaRequest request) {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        
        Pausa pausa = pausaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pausa não encontrada com id: " + id));
        
        // verifica se a pausa pertence ao usuário logado
        if (!pausa.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("pausa não encontrada com id: " + id);
        }

        if (request.getInicio() != null) {
            pausa.setInicio(request.getInicio());
        }
        if (request.getFim() != null) {
            pausa.setFim(request.getFim());
            pausa.calcularDuracao();
            
            // chama procedure oracle para encerrar pausa
            oracleProcedureService.encerrarPausa(pausa.getFim(), pausa.getDuracao());
        }

        Pausa savedPausa = pausaRepository.save(pausa);
        return pausaMapper.toResponse(savedPausa);
    }

    @Transactional
    public void deletarPausa(@NonNull UUID id) {
        String emailUsuario = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()).getName();
        Usuario usuario = usuarioService.buscarPorEmail(emailUsuario);
        
        Pausa pausa = pausaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("pausa não encontrada com id: " + id));
        
        // verifica se a pausa pertence ao usuário logado
        if (!pausa.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("pausa não encontrada com id: " + id);
        }
        
        pausaRepository.deleteById(id);
    }
}

