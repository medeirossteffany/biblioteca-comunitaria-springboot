package com.biblioteca.repository;
import com.biblioteca.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositório para operações de acesso a dados de usuários.
 */

public interface UsuarioRepository extends JpaRepository<Usuario, Long> { }
