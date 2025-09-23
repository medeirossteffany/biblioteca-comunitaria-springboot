package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositório para operações de acesso a dados de empréstimos.
 * Fornece métodos para buscar empréstimos ativos, verificar vínculos
 * e excluir registros inativos.
 */

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByAtivoTrue();

    boolean existsByLivroIdAndAtivoTrue(Long livroId);
    boolean existsByUsuarioIdAndAtivoTrue(Long usuarioId);

    @Modifying
    void deleteByLivroIdAndAtivoFalse(Long livroId);

    @Modifying
    void deleteByUsuarioIdAndAtivoFalse(Long usuarioId);
}
