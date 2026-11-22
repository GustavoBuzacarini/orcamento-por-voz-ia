package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.ValidadorDeGasto;
import org.springframework.stereotype.Service;

@Service
public class ConsultarGastosUseCase {

    private final ValidadorDeGasto validador;
    private final AvaliarLimiteService limites;
    private final Calendario calendario;

    public ConsultarGastosUseCase(ValidadorDeGasto validador, AvaliarLimiteService limites, Calendario calendario) {
        this.validador = validador;
        this.limites = limites;
        this.calendario = calendario;
    }

    public String totalPorCategoria(String categoriaTexto) {
        Categoria categoria = validador.validarCategoria(categoriaTexto);
        return "Em " + categoria.rotulo() + " você gastou "
                + Moeda.formatar(limites.totalDoMes(categoria, calendario.hoje())) + " neste mês.";
    }
}
