package br.edu.oficina.service;

import br.edu.oficina.model.StatusOrdem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OrdemDados(
    @NotNull @Positive Long clienteId,
    @NotNull @Positive Long equipamentoId,
    @NotNull @Positive Long tecnicoId,
    @NotNull @Positive Long servicoId,
    @NotBlank @Size(max = 500) String descricao,
    @NotNull StatusOrdem status
) {
}
