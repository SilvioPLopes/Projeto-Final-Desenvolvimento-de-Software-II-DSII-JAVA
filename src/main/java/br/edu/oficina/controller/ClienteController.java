package br.edu.oficina.controller;

import br.edu.oficina.service.ClienteDados;
import br.edu.oficina.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clientes;

    public ClienteController(ClienteService clientes) {
        this.clientes = clientes;
    }

    @GetMapping
    String listar(Model model) {
        model.addAttribute("clientes", clientes.listar());
        return "clientes";
    }

    @GetMapping("/novo")
    String novo(Model model) {
        model.addAttribute("dados", new ClienteDados("", "", "", "", "", "", ""));
        return formulario(model, null);
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        model.addAttribute("dados", ClienteDados.de(clientes.buscar(id)));
        return formulario(model, id);
    }

    @PostMapping({"/novo", "/{id}/editar"})
    String salvar(@PathVariable(required = false) Long id, @Valid @ModelAttribute("dados") ClienteDados dados,
                  BindingResult erros, Model model, RedirectAttributes redirect) {
        if (erros.hasErrors()) {
            return formulario(model, id);
        }
        clientes.salvar(id, dados);
        redirect.addFlashAttribute("mensagem", "Cliente salvo.");
        return "redirect:/clientes";
    }

    @PostMapping("/{id}/excluir")
    String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        clientes.excluir(id);
        redirect.addFlashAttribute("mensagem", "Cliente excluído.");
        return "redirect:/clientes";
    }

    private String formulario(Model model, Long id) {
        model.addAttribute("id", id);
        return "cliente-form";
    }
}
