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

/** Gasto proposto que espera "sim" ou "não". Existe no máximo um (ASM-003). */
@Entity
public class GastoPendente {

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
    private Instant expiraEm;

    protected GastoPendente() {
    }

    public GastoPendente(BigDecimal valor, Categoria categoria, String descricao, LocalDate data, Instant expiraEm) {
        this.valor = valor;
        this.categoria = categoria;
        this.descricao = descricao;
        this.data = data;
        this.expiraEm = expiraEm;
    }

    public boolean expirado(Instant agora) {
        return !expiraEm.isAfter(agora);
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

    public Instant getExpiraEm() {
        return expiraEm;
    }
}
