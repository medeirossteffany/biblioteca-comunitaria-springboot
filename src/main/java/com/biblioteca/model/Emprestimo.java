package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Livro é obrigatório")
    private Livro livro;

    @ManyToOne
    @NotNull(message = "Usuário é obrigatório")
    private Usuario usuario;

    @NotNull(message = "Data de retirada é obrigatória")
    private LocalDate dataRetirada;

    @NotNull(message = "Data de devolução é obrigatória")
    private LocalDate dataDevolucao;

    private boolean ativo = true;

    // getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public LocalDate getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(LocalDate dataRetirada) { this.dataRetirada = dataRetirada; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
