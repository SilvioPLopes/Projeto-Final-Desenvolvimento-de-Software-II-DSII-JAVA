package br.edu.oficina.controller;

import br.edu.oficina.service.CepService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final CepService cepService;

    public ApiController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/cep/{cep}")
    CepService.EnderecoCep consultarCep(@PathVariable String cep) {
        return cepService.consultar(cep);
    }
}
