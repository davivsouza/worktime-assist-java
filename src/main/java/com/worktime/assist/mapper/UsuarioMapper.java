package com.worktime.assist.mapper;

import com.worktime.assist.dto.UsuarioRequest;
import com.worktime.assist.dto.UsuarioResponse;
import com.worktime.assist.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        if (request == null) {
            return null;
        }

        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(request.getSenha()) // será criptografada no service
                .build();
    }

    public UsuarioResponse toResponse(Usuario entity) {
        if (entity == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .email(entity.getEmail())
                .build();
    }
}

