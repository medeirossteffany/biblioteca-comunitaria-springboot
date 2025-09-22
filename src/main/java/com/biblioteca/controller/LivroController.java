package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("livros", livroRepository.findAll());
        return "livros/lista";
    }

    @GetMapping("/disponiveis")
    public String listarDisponiveis(Model model) {
        model.addAttribute("livros", livroRepository.findByDisponivelTrue());
        return "livros/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("livro", new Livro());
        return "livros/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Livro livro, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "livros/form";
        }

        // Validação do ano de publicação no futuro
        if (livro.getAnoPublicacao() != null && livro.getAnoPublicacao() > java.time.Year.now().getValue()) {
            result.rejectValue("anoPublicacao", "error.livro", "O ano de publicação não pode ser no futuro.");
            return "livros/form";
        }

        try {
            livroRepository.save(livro);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar livro: " + e.getMessage());
        }

        return "redirect:/livros";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Livro livro = livroRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
            model.addAttribute("livro", livro);
            return "livros/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar livro: " + e.getMessage());
            return "redirect:/livros";
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!livroRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Livro não encontrado.");
                return "redirect:/livros";
            }

            // Verificar se o livro está vinculado a algum empréstimo
            boolean temEmprestimos = emprestimoRepository.existsByLivroId(id);
            if (temEmprestimos) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível excluir um livro vinculado a um empréstimo.");
                return "redirect:/livros";
            }

            livroRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Livro excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir livro: " + e.getMessage());
        }

        return "redirect:/livros";
    }
}
