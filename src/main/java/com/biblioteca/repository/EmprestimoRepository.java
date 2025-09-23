package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByAtivoTrue();

    // Métodos para verificar empréstimos ativos
    boolean existsByLivroIdAndAtivoTrue(Long livroId);
    boolean existsByUsuarioIdAndAtivoTrue(Long usuarioId);

    // Métodos para excluir empréstimos inativos
    @Modifying
    void deleteByLivroIdAndAtivoFalse(Long livroId);

    @Modifying
    void deleteByUsuarioIdAndAtivoFalse(Long usuarioId);
}
