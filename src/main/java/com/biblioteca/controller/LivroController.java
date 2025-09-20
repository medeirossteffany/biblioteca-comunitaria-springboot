package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private LivroRepository livroRepository;

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
    public String salvar(@Valid @ModelAttribute Livro livro, BindingResult result, Model model) {
        if (result.hasErrors()) return "livros/form";
        // Ao cadastrar, já inicia disponível (campo default)
        livroRepository.save(livro);
        return "redirect:/livros";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        model.addAttribute("livro", livro);
        return "livros/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        livroRepository.deleteById(id);
        return "redirect:/livros";
    }
}
