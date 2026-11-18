package br.edu.oficina.service;

import br.edu.oficina.model.OrdemServico;
import br.edu.oficina.repository.OrdemServicoRepository;
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

    public OrdemService(OrdemServicoRepository ordens) {
        this.ordens = ordens;
    }

    public List<OrdemServico> listar() {
        return ordens.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public OrdemServico buscar(Long id) {
        return ordens.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordem não encontrada."));
    }
}
