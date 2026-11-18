package br.edu.oficina.service;

import br.edu.oficina.model.*;
import br.edu.oficina.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class OrdemService {
    private final OrdemServicoRepository ordens;
    private final ClienteService clientes;
    private final EquipamentoRepository equipamentos;
    private final TecnicoRepository tecnicos;
    private final ServicoRepository servicos;

    public OrdemService(OrdemServicoRepository ordens, ClienteService clientes, EquipamentoRepository equipamentos,
                        TecnicoRepository tecnicos, ServicoRepository servicos) {
        this.ordens = ordens;
        this.clientes = clientes;
        this.equipamentos = equipamentos;
        this.tecnicos = tecnicos;
        this.servicos = servicos;
    }

    public List<OrdemServico> listar() {
        return ordens.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public OrdemServico buscar(Long id) {
        return ordens.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem não encontrada."));
    }

    public List<Equipamento> equipamentos() {
        return equipamentos.findAll(Sort.by("modelo"));
    }

    public List<Tecnico> tecnicos() {
        return tecnicos.findAll(Sort.by("nome"));
    }

    public List<Servico> servicos() {
        return servicos.findAll(Sort.by("nome"));
    }

    @Transactional
    public void criar(OrdemDados dados) {
        Cliente cliente = clientes.buscar(dados.clienteId());
        Equipamento equipamento = equipamentos.findById(dados.equipamentoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipamento inválido."));
        Tecnico tecnico = tecnicos.findById(dados.tecnicoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Técnico inválido."));
        Servico servico = servicos.findById(dados.servicoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Serviço inválido."));

        ordens.save(new OrdemServico(cliente, equipamento, tecnico, servico, dados.descricao().strip(), dados.status()));
    }
}
