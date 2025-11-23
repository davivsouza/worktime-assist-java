package com.worktime.assist.service;

import com.worktime.assist.dto.UsuarioRequest;
import com.worktime.assist.dto.UsuarioResponse;
import com.worktime.assist.entity.Usuario;
import com.worktime.assist.exception.ResourceConflictException;
import com.worktime.assist.exception.ResourceNotFoundException;
import com.worktime.assist.mapper.UsuarioMapper;
import com.worktime.assist.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponse criarUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("email já cadastrado: " + request.getEmail());
        }

        Usuario usuario = usuarioMapper.toEntity(request);
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        
        usuario = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(@NonNull String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com email: " + email));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(@NonNull UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("usuário não encontrado com id: " + id));
        return usuarioMapper.toResponse(usuario);
    }
}

