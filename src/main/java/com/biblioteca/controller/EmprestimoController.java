package com.biblioteca.controller;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "emprestimos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Emprestimo emprestimo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // Recarregar listas para o formulário em caso de erro
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());
        model.addAttribute("usuarios", usuarioRepository.findAll());

        if (result.hasErrors()) {
            return "emprestimos/form";
        }

        // Validação de datas
        LocalDate retirada = emprestimo.getDataRetirada();
        LocalDate devolucao = emprestimo.getDataDevolucao();
        LocalDate hoje = LocalDate.now();

        // Validar se a data de retirada não é no futuro
        if (retirada != null && retirada.isAfter(hoje)) {
            result.rejectValue("dataRetirada", "error.emprestimo", "A data de retirada não pode ser no futuro.");
            return "emprestimos/form";
        }

        // Validar se a data de devolução é posterior à data de retirada
        if (retirada == null || devolucao == null || !devolucao.isAfter(retirada)) {
            result.rejectValue("dataDevolucao", "error.emprestimo", "A data de devolução deve ser posterior à data de retirada.");
            return "emprestimos/form";
        }

        try {

            Usuario usuario = usuarioRepository.findById(emprestimo.getUsuario().getId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            Livro livro = livroRepository.findById(emprestimo.getLivro().getId())
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

            if (!livro.isDisponivel()) {
                result.rejectValue("livro", "error.emprestimo", "Livro não disponível.");
                return "emprestimos/form";
            }

            // Definir as entidades completas no empréstimo
            emprestimo.setUsuario(usuario);
            emprestimo.setLivro(livro);
            emprestimo.setAtivo(true);

            // Marcar livro como emprestado e salvar empréstimo
            livro.setDisponivel(false);
            livroRepository.save(livro);
            emprestimoRepository.save(emprestimo);

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Empréstimo criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao criar empréstimo: " + e.getMessage());
        }

        return "redirect:/emprestimos";
    }

    @GetMapping("/devolver/{id}")
    public String devolver(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            Emprestimo emp = emprestimoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

            if (emp.isAtivo()) {
                emp.setAtivo(false);
                emprestimoRepository.save(emp);

                Livro livro = emp.getLivro();
                livro.setDisponivel(true);
                livroRepository.save(livro);

                redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro devolvido com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("mensagemErro", "Este empréstimo já foi finalizado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao devolver livro: " + e.getMessage());
        }

        return "redirect:/emprestimos";
    }
}
