package br.edu.oficina.model;

import jakarta.persistence.*;

@Entity
public class OrdemServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Equipamento equipamento;

    @ManyToOne(optional = false)
    private Tecnico tecnico;

    @ManyToOne(optional = false)
    private Servico servico;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrdem status;

    protected OrdemServico() {
    }

    public OrdemServico(Cliente cliente, Equipamento equipamento, Tecnico tecnico, Servico servico, String descricao, StatusOrdem status) {
        atualizar(cliente, equipamento, tecnico, servico, descricao, status);
    }

    public void atualizar(Cliente cliente, Equipamento equipamento, Tecnico tecnico, Servico servico, String descricao, StatusOrdem status) {
        this.cliente = cliente;
        this.equipamento = equipamento;
        this.tecnico = tecnico;
        this.servico = servico;
        this.descricao = descricao;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public Tecnico getTecnico() {
        return tecnico;
    }

    public Servico getServico() {
        return servico;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusOrdem getStatus() {
        return status;
    }
}
