package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Gasto;
import br.com.orcamentovoz.domain.GastoPendente;
import br.com.orcamentovoz.infrastructure.persistence.GastoPendenteRepository;
import br.com.orcamentovoz.infrastructure.persistence.GastoRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Único lugar que grava um gasto: só depois do "sim" e só se a pendência não expirou. */
@Service
public class ConfirmarGastoUseCase {

    static final String SEM_PENDENCIA = "Não há gasto para confirmar. Fale o gasto de novo.";

    private final GastoPendenteRepository pendentes;
    private final GastoRepository gastos;
    private final AvaliarLimiteService limites;
    private final Calendario calendario;

    public ConfirmarGastoUseCase(GastoPendenteRepository pendentes, GastoRepository gastos,
                                 AvaliarLimiteService limites, Calendario calendario) {
        this.pendentes = pendentes;
        this.gastos = gastos;
        this.limites = limites;
        this.calendario = calendario;
    }

    @Transactional
    public String confirmar() {
        Optional<GastoPendente> pendente = pendenteValido();
        if (pendente.isEmpty()) {
            return SEM_PENDENCIA;
        }
        GastoPendente p = pendente.get();
        gastos.save(new Gasto(p.getValor(), p.getCategoria(), p.getDescricao(), p.getData(), calendario.agora()));
        pendentes.deleteAll();
        String base = "Gasto de " + Moeda.formatar(p.getValor()) + " em " + p.getCategoria().rotulo() + " gravado.";
        String limite = limites.mensagemApos(p.getCategoria(), p.getData());
        return limite.isEmpty() ? base : base + " " + limite;
    }

    @Transactional
    public String descartar() {
        Optional<GastoPendente> pendente = pendenteValido();
        if (pendente.isEmpty()) {
            return SEM_PENDENCIA;
        }
        GastoPendente p = pendente.get();
        pendentes.deleteAll();
        return "Certo, descartei o gasto de " + Moeda.formatar(p.getValor()) + " em "
                + p.getCategoria().rotulo() + ". Nada foi gravado.";
    }

    /** Pendência expirada é apagada e tratada como inexistente (AC-004). */
    private Optional<GastoPendente> pendenteValido() {
        Optional<GastoPendente> pendente = pendentes.findAll().stream().findFirst();
        if (pendente.isPresent() && pendente.get().expirado(calendario.agora())) {
            pendentes.deleteAll();
            return Optional.empty();
        }
        return pendente;
    }
}
