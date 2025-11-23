package com.worktime.assist.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PausaRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime inicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime fim;
}

