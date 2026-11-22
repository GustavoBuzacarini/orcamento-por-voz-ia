package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.LimiteCategoria;
import br.com.orcamentovoz.domain.ValidadorDeGasto;
import br.com.orcamentovoz.infrastructure.persistence.LimiteRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefinirLimiteUseCase {

    private final ValidadorDeGasto validador;
    private final LimiteRepository limites;

    public DefinirLimiteUseCase(ValidadorDeGasto validador, LimiteRepository limites) {
        this.validador = validador;
        this.limites = limites;
    }

    @Transactional
    public String definir(String categoriaTexto, BigDecimal valor) {
        BigDecimal valorValido = validador.validarValor(valor);
        Categoria categoria = validador.validarCategoria(categoriaTexto);
        limites.save(new LimiteCategoria(categoria, valorValido));
        return "Limite de " + categoria.rotulo() + ": " + Moeda.formatar(valorValido) + " por mês.";
    }
}
