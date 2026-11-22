package br.com.orcamentovoz.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.math.BigDecimal;

/** Limite mensal de uma categoria (ASM-006). Redefinir substitui o anterior. */
@Entity
public class LimiteCategoria {

    @Id
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorMensal;

    protected LimiteCategoria() {
    }

    public LimiteCategoria(Categoria categoria, BigDecimal valorMensal) {
        this.categoria = categoria;
        this.valorMensal = valorMensal;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public BigDecimal getValorMensal() {
        return valorMensal;
    }
}
