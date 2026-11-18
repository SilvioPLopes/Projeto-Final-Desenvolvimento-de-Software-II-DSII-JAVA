package br.edu.oficina.model;

import jakarta.persistence.*;

@Entity
public class Equipamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String modelo;

    @ManyToOne(optional = false)
    private Categoria categoria;

    protected Equipamento() {
    }

    public Equipamento(String modelo, Categoria categoria) {
        this.modelo = modelo;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getModelo() {
        return modelo;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
