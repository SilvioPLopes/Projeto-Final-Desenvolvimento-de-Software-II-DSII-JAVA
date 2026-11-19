package br.edu.oficina.controller;

import br.edu.oficina.model.OrdemServico;
import br.edu.oficina.service.CepService;
import br.edu.oficina.service.OrdemService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final OrdemService ordens;
    private final CepService cepService;

    public ApiController(OrdemService ordens, CepService cepService) {
        this.ordens = ordens;
        this.cepService = cepService;
    }

    @GetMapping("/ordens")
    @Operation(summary = "Lista as ordens de serviço")
    List<OrdemResposta> listar() {
        return ordens.listar().stream().map(OrdemResposta::de).toList();
    }

    @GetMapping("/ordens/{id}")
    @Operation(summary = "Consulta uma ordem pelo ID")
    OrdemResposta buscar(@PathVariable Long id) {
        return OrdemResposta.de(ordens.buscar(id));
    }

    @GetMapping("/cep/{cep}")
    @Operation(summary = "Consulta um endereço pelo ViaCEP (perfil atendente)")
    CepService.EnderecoCep consultarCep(@PathVariable String cep) {
        return cepService.consultar(cep);
    }

    public record OrdemResposta(Long id, String cliente, String equipamento, String categoria,
                                String tecnico, String servico, String descricao, String status) {
        static OrdemResposta de(OrdemServico ordem) {
            return new OrdemResposta(ordem.getId(), ordem.getCliente().getNome(),
                ordem.getEquipamento().getModelo(), ordem.getEquipamento().getCategoria().getNome(),
                ordem.getTecnico().getNome(), ordem.getServico().getNome(),
                ordem.getDescricao(), ordem.getStatus().name());
        }
    }
}
