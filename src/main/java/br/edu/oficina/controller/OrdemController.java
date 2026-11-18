package br.edu.oficina.controller;

import br.edu.oficina.model.StatusOrdem;
import br.edu.oficina.service.ClienteService;
import br.edu.oficina.service.OrdemDados;
import br.edu.oficina.service.OrdemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ordens")
public class OrdemController {
    private final OrdemService ordens;
    private final ClienteService clientes;

    public OrdemController(OrdemService ordens, ClienteService clientes) {
        this.ordens = ordens;
        this.clientes = clientes;
    }

    @GetMapping
    String listar(Model model) {
        model.addAttribute("ordens", ordens.listar());
        return "ordens";
    }

    @GetMapping("/{id}")
    String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("ordem", ordens.buscar(id));
        return "ordem-detalhe";
    }

    @GetMapping("/nova")
    String nova(Model model) {
        model.addAttribute("dados", new OrdemDados(null, null, null, null, "", StatusOrdem.ABERTA));
        return formulario(model, null);
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        model.addAttribute("dados", OrdemDados.de(ordens.buscar(id)));
        return formulario(model, id);
    }

    @PostMapping({"/nova", "/{id}/editar"})
    String salvar(@PathVariable(required = false) Long id, @Valid @ModelAttribute("dados") OrdemDados dados,
                  BindingResult erros, Model model, RedirectAttributes redirect) {
        if (erros.hasErrors()) {
            return formulario(model, id);
        }
        ordens.salvar(id, dados);
        redirect.addFlashAttribute("mensagem", "Ordem salva.");
        return "redirect:/ordens";
    }

    @PostMapping("/{id}/excluir")
    String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        ordens.excluir(id);
        redirect.addFlashAttribute("mensagem", "Ordem excluída.");
        return "redirect:/ordens";
    }

    private String formulario(Model model, Long id) {
        model.addAttribute("id", id);
        model.addAttribute("clientes", clientes.listar());
        model.addAttribute("equipamentos", ordens.equipamentos());
        model.addAttribute("tecnicos", ordens.tecnicos());
        model.addAttribute("servicos", ordens.servicos());
        model.addAttribute("status", StatusOrdem.values());
        return "ordem-form";
    }
}
