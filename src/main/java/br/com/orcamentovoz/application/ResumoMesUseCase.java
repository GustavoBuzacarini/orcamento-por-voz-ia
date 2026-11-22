package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.Gasto;
import br.com.orcamentovoz.infrastructure.persistence.GastoRepository;
import br.com.orcamentovoz.infrastructure.persistence.LimiteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ResumoMesUseCase {

    private final GastoRepository gastos;
    private final LimiteRepository limites;
    private final Calendario calendario;

    public ResumoMesUseCase(GastoRepository gastos, LimiteRepository limites, Calendario calendario) {
        this.gastos = gastos;
        this.limites = limites;
        this.calendario = calendario;
    }

    public String resumo() {
        LocalDate hoje = calendario.hoje();
        List<Gasto> doMes = gastos.findByDataBetweenOrderByDataAscIdAsc(
                calendario.inicioDoMes(hoje), calendario.fimDoMes(hoje));
        if (doMes.isEmpty()) {
            return "Você ainda não registrou gastos neste mês.";
        }
        Map<Categoria, BigDecimal> porCategoria = new EnumMap<>(Categoria.class);
        BigDecimal total = BigDecimal.ZERO;
        for (Gasto g : doMes) {
            porCategoria.merge(g.getCategoria(), g.getValor(), BigDecimal::add);
            total = total.add(g.getValor());
        }
        StringBuilder fala = new StringBuilder("Neste mês você gastou ")
                .append(Moeda.formatar(total)).append(" no total. ");
        List<String> acima = new ArrayList<>();
        porCategoria.forEach((categoria, soma) -> {
            fala.append(capitalizar(categoria.rotulo())).append(": ").append(Moeda.formatar(soma)).append(". ");
            limites.findById(categoria).filter(l -> soma.compareTo(l.getValorMensal()) > 0)
                    .ifPresent(l -> acima.add(categoria.rotulo()));
        });
        if (!acima.isEmpty()) {
            fala.append("Acima do limite: ").append(String.join(", ", acima)).append(".");
        }
        return fala.toString().trim();
    }

    private static String capitalizar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
