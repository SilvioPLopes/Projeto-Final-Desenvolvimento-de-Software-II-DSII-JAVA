package br.edu.oficina.model;

import jakarta.persistence.*;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 25)
    private String telefone;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    private Endereco endereco;

    protected Cliente() {
    }

    public Cliente(String nome, String telefone, Endereco endereco) {
        atualizar(nome, telefone, endereco);
    }

    public void atualizar(String nome, String telefone, Endereco endereco) {
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }
}
