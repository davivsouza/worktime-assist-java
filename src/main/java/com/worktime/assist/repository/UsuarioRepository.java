package com.worktime.assist.repository;

import com.worktime.assist.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    @NonNull
    Optional<Usuario> findByEmail(@NonNull String email);

    boolean existsByEmail(String email);
}

