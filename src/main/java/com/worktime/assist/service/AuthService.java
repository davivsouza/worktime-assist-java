package com.worktime.assist.service;

import com.worktime.assist.config.JwtTokenProvider;
import com.worktime.assist.dto.LoginRequest;
import com.worktime.assist.dto.LoginResponse;
import com.worktime.assist.entity.Usuario;
import com.worktime.assist.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioService usuarioService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String email = request.getUsername();
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (!passwordEncoder.matches(request.getPassword(), usuario.getSenha())) {
            throw new ResourceNotFoundException("credenciais inválidas");
        }

        String token = jwtTokenProvider.generateToken(usuario.getEmail());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .build();
    }
}

