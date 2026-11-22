package br.com.orcamentovoz.domain;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Regras que o código decide, nunca o modelo (P-003): valor positivo, categoria conhecida e data
 * que não esteja no futuro.
 */
@Component
public class ValidadorDeGasto {

    private static final BigDecimal MAXIMO = new BigDecimal("9999999999.99");

    private final Clock relogio;

    public ValidadorDeGasto(Clock relogio) {
        this.relogio = relogio;
    }

    public BigDecimal validarValor(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new RegraDeGastoException("O valor precisa ser maior que zero.");
        }
        if (valor.scale() > 2 && valor.stripTrailingZeros().scale() > 2) {
            throw new RegraDeGastoException("O valor deve ter no máximo duas casas decimais, ou seja, centavos.");
        }
        if (valor.compareTo(MAXIMO) > 0) {
            throw new RegraDeGastoException("Esse valor é alto demais para registrar.");
        }
        return valor.setScale(2, java.math.RoundingMode.UNNECESSARY);
    }

    public Categoria validarCategoria(String texto) {
        return Categoria.deTexto(texto).orElseThrow(() -> new RegraDeGastoException(
                "A categoria " + (texto == null || texto.isBlank() ? "informada" : texto)
                        + " não existe. As categorias são: " + Categoria.listaParaFala() + "."));
    }

    public LocalDate validarData(LocalDate data) {
        if (data.isAfter(LocalDate.now(relogio))) {
            throw new RegraDeGastoException("Não dá para registrar um gasto em data futura.");
        }
        return data;
    }
}
