package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.LimiteCategoria;
import br.com.orcamentovoz.infrastructure.persistence.GastoRepository;
import br.com.orcamentovoz.infrastructure.persistence.LimiteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Service;

/** Compara o gasto do mês com o limite da categoria. O limite só avisa; nunca bloqueia. */
@Service
public class AvaliarLimiteService {

    private final LimiteRepository limites;
    private final GastoRepository gastos;
    private final Calendario calendario;

    public AvaliarLimiteService(LimiteRepository limites, GastoRepository gastos, Calendario calendario) {
        this.limites = limites;
        this.gastos = gastos;
        this.calendario = calendario;
    }

    public BigDecimal totalDoMes(Categoria categoria, LocalDate data) {
        return gastos.somaDaCategoria(categoria, calendario.inicioDoMes(data), calendario.fimDoMes(data));
    }

    /** Quanto passaria do limite se este valor fosse gravado; vazio se não passa ou não há limite. */
    public Optional<BigDecimal> excessoSeGravar(Categoria categoria, BigDecimal valor, LocalDate data) {
        return limites.findById(categoria)
                .map(LimiteCategoria::getValorMensal)
                .map(limite -> totalDoMes(categoria, data).add(valor).subtract(limite))
                .filter(excesso -> excesso.signum() > 0);
    }

    /** Frase para dizer depois de gravar: quanto resta, ou aviso de que passou. Vazio sem limite. */
    public String mensagemApos(Categoria categoria, LocalDate data) {
        return limites.findById(categoria).map(limite -> {
            BigDecimal diferenca = limite.getValorMensal().subtract(totalDoMes(categoria, data));
            if (diferenca.signum() < 0) {
                return "Atenção: o limite de " + categoria.rotulo() + " foi ultrapassado em "
                        + Moeda.formatar(diferenca.negate()) + ".";
            }
            return "Ainda restam " + Moeda.formatar(diferenca) + " no limite de " + categoria.rotulo() + ".";
        }).orElse("");
    }
}
