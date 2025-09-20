package com.biblioteca.controller;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listar(Model model) {
        List<Emprestimo> ativos = emprestimoRepository.findByAtivoTrue();
        model.addAttribute("emprestimos", ativos);
        return "emprestimos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("emprestimo", new Emprestimo());
        // só listar livros disponíveis
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "emprestimos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Emprestimo emprestimo, BindingResult result, Model model) {
        // validações básicas
        if (result.hasErrors()) {
            model.addAttribute("livros", livroRepository.findByDisponivelTrue());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "emprestimos/form";
        }

        // checar datas: devolução deve ser posterior à retirada
        LocalDate retirada = emprestimo.getDataRetirada();
        LocalDate devolucao = emprestimo.getDataDevolucao();
        if (retirada == null || devolucao == null || !devolucao.isAfter(retirada)) {
            result.rejectValue("dataDevolucao", "error.emprestimo", "A data de devolução deve ser posterior à data de retirada.");
            model.addAttribute("livros", livroRepository.findByDisponivelTrue());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "emprestimos/form";
        }

        // checar se livro está disponível
        Livro livro = livroRepository.findById(emprestimo.getLivro().getId())
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (!livro.isDisponivel()) {
            result.rejectValue("livro", "error.emprestimo", "Livro não disponível.");
            model.addAttribute("livros", livroRepository.findByDisponivelTrue());
            model.addAttribute("usuarios", usuarioRepository.findAll());
            return "emprestimos/form";
        }

        // marcar livro como emprestado e salvar empréstimo
        livro.setDisponivel(false);
        livroRepository.save(livro);

        emprestimo.setAtivo(true);
        emprestimoRepository.save(emprestimo);

        return "redirect:/emprestimos";
    }

    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable Long id) {
        Emprestimo emp = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emp.isAtivo()) {
            emp.setAtivo(false);
            emprestimoRepository.save(emp);

            Livro livro = emp.getLivro();
            livro.setDisponivel(true);
            livroRepository.save(livro);
        }
        return "redirect:/emprestimos";
    }
}
