package br.edu.oficina;

import br.edu.oficina.model.Cliente;
import br.edu.oficina.model.Endereco;
import br.edu.oficina.repository.ClienteRepository;
import br.edu.oficina.repository.OrdemServicoRepository;
import br.edu.oficina.service.ClienteDados;
import br.edu.oficina.service.ClienteService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {
    @Mock ClienteRepository clientes;
    @Mock OrdemServicoRepository ordens;
    @InjectMocks ClienteService service;
    @Captor ArgumentCaptor<Cliente> clienteSalvo;

    @Test
    void normalizaCepUfENomeAntesDeSalvar() {
        var dados = new ClienteDados(" Lia Souza ", "11999990000", "01001-000", "Praça da Sé", "10", "São Paulo", "sp");

        service.salvar(null, dados);

        verify(clientes).save(clienteSalvo.capture());
        Cliente cliente = clienteSalvo.getValue();
        assertThat(cliente.getNome()).isEqualTo("Lia Souza");
        assertThat(cliente.getEndereco().getCep()).isEqualTo("01001000");
        assertThat(cliente.getEndereco().getUf()).isEqualTo("SP");
    }

    @Test
    void impedeExcluirClienteComOrdem() {
        var cliente = new Cliente("Lia Souza", "11999990000", new Endereco("01001000", "Praça da Sé", "10", "São Paulo", "SP"));
        when(clientes.findById(1L)).thenReturn(Optional.of(cliente));
        when(ordens.existsByCliente_Id(1L)).thenReturn(true);

        assertThat(service.excluir(1L)).isFalse();

        verify(clientes, never()).delete(cliente);
    }
}
