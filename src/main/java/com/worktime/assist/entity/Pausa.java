package com.worktime.assist.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pausas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pausa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "RAW(16)")
    private UUID id;

    @NotNull(message = "inicio é obrigatório")
    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fim")
    private LocalDateTime fim;

    @Column(name = "duracao")
    private Long duracao; // duração em segundos

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "usuário é obrigatório")
    private Usuario usuario;

    @PrePersist
    protected void onCreate() {
        if (inicio == null) {
            inicio = LocalDateTime.now();
        }
    }

    public void calcularDuracao() {
        if (inicio != null && fim != null) {
            this.duracao = java.time.Duration.between(inicio, fim).getSeconds();
        }
    }
}

