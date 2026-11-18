package br.edu.oficina.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PaginaController {
    @GetMapping("/")
    String inicio() {
        return "redirect:/ordens";
    }

    @GetMapping("/saude")
    @ResponseBody
    String saude() {
        return "OK";
    }
}
