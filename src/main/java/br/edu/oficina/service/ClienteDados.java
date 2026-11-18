package br.edu.oficina.service;

import br.edu.oficina.model.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteDados(
    @NotBlank @Size(max = 100) String nome,
    @NotBlank @Size(max = 25) String telefone,
    @NotBlank @Pattern(regexp = "[0-9]{5}-?[0-9]{3}", message = "Informe um CEP com 8 dígitos.") String cep,
    @NotBlank @Size(max = 120) String logradouro,
    @NotBlank @Size(max = 20) String numero,
    @NotBlank @Size(max = 100) String cidade,
    @NotBlank @Pattern(regexp = "[A-Za-z]{2}", message = "Informe a sigla do estado.") String uf
) {
    public static ClienteDados de(Cliente cliente) {
        var endereco = cliente.getEndereco();
        return new ClienteDados(cliente.getNome(), cliente.getTelefone(), endereco.getCep(),
            endereco.getLogradouro(), endereco.getNumero(), endereco.getCidade(), endereco.getUf());
    }
}
