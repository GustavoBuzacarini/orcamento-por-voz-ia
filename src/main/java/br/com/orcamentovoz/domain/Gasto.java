package br.com.orcamentovoz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Gasto já confirmado pela pessoa usuária. */
@Entity
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private Instant criadoEm;

    protected Gasto() {
    }

    public Gasto(BigDecimal valor, Categoria categoria, String descricao, LocalDate data, Instant criadoEm) {
        this.valor = valor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.data = data;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDate getData() {
        return data;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
