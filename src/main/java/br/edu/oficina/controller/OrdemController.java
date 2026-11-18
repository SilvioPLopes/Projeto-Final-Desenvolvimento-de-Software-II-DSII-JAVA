package br.edu.oficina.controller;

import br.edu.oficina.service.OrdemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ordens")
public class OrdemController {
    private final OrdemService ordens;

    public OrdemController(OrdemService ordens) {
        this.ordens = ordens;
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
}
