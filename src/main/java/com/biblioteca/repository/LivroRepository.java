package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositório para operações de acesso a dados de livros.
 * Inclui método para buscar apenas os disponíveis.
 */

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByDisponivelTrue();
}
