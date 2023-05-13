package com.lbraz.meusgastosapi.domain.repository;

import com.lbraz.meusgastosapi.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByEmail(String email);
}
