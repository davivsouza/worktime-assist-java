package com.worktime.assist.mapper;

import com.worktime.assist.dto.PausaRequest;
import com.worktime.assist.dto.PausaResponse;
import com.worktime.assist.entity.Pausa;
import org.springframework.stereotype.Component;

@Component
public class PausaMapper {

    public Pausa toEntity(PausaRequest request) {
        if (request == null) {
            return null;
        }

        return Pausa.builder()
                .inicio(request.getInicio())
                .fim(request.getFim())
                .build();
    }

    public PausaResponse toResponse(Pausa entity) {
        if (entity == null) {
            return null;
        }

        return PausaResponse.builder()
                .id(entity.getId())
                .inicio(entity.getInicio())
                .fim(entity.getFim())
                .duracao(entity.getDuracao())
                .build();
    }
}

