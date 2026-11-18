package br.edu.oficina.repository;

import br.edu.oficina.model.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    boolean existsByCliente_Id(Long clienteId);
}
