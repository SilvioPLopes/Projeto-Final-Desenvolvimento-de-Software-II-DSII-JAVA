package br.edu.oficina.service;

import br.edu.oficina.model.Cliente;
import br.edu.oficina.model.Endereco;
import br.edu.oficina.repository.ClienteRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> listar() {
        return clientes.findAll(Sort.by("nome", "id"));
    }

    public Cliente buscar(Long id) {
        return clientes.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado."));
    }

    @Transactional
    public void salvar(Long id, ClienteDados dados) {
        var endereco = new Endereco(dados.cep().replace("-", ""), dados.logradouro().strip(),
            dados.numero().strip(), dados.cidade().strip(), dados.uf().toUpperCase(Locale.ROOT));

        if (id == null) {
            clientes.save(new Cliente(dados.nome().strip(), dados.telefone().strip(), endereco));
        } else {
            buscar(id).atualizar(dados.nome().strip(), dados.telefone().strip(), endereco);
        }
    }

    @Transactional
    public void excluir(Long id) {
        clientes.delete(buscar(id));
    }
}
