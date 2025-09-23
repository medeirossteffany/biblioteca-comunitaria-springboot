package com.biblioteca.controller;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "usuarios/form";
        }

        try {
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao salvar usuário: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            model.addAttribute("usuario", usuario);
            return "usuarios/form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao carregar usuário: " + e.getMessage());
            return "redirect:/usuarios";
        }
    }

    @GetMapping("/excluir/{id}")
    @Transactional
    public String excluir(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            if (!usuarioRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Usuário não encontrado.");
                return "redirect:/usuarios";
            }

            // Verificar se o usuário tem empréstimos ATIVOS
            boolean temEmprestimosAtivos = emprestimoRepository.existsByUsuarioIdAndAtivoTrue(id);
            if (temEmprestimosAtivos) {
                redirectAttributes.addFlashAttribute("mensagemErro", "Não é possível excluir um usuário vinculado a um empréstimo ativo.");
                return "redirect:/usuarios";
            }

            // Excluir primeiro todos os empréstimos inativos deste usuário
            emprestimoRepository.deleteByUsuarioIdAndAtivoFalse(id);

            // Agora excluir o usuário
            usuarioRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Usuário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Erro ao excluir usuário: " + e.getMessage());
        }

        return "redirect:/usuarios";
    }
}
