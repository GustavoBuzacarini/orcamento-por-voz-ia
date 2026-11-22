package br.com.orcamentovoz.application;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.GastoPendente;
import br.com.orcamentovoz.domain.RegraDeGastoException;
import br.com.orcamentovoz.domain.ValidadorDeGasto;
import br.com.orcamentovoz.infrastructure.persistence.GastoPendenteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Propõe um gasto: valida em código e deixa pendente. Nada vai para a tabela de gastos aqui. */
@Service
public class RegistrarGastoUseCase {

    private static final DateTimeFormatter DATA_FALADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ValidadorDeGasto validador;
    private final GastoPendenteRepository pendentes;
    private final AvaliarLimiteService limites;
    private final Calendario calendario;
    private final long minutosDeValidade;

    public RegistrarGastoUseCase(ValidadorDeGasto validador, GastoPendenteRepository pendentes,
                                 AvaliarLimiteService limites, Calendario calendario,
                                 @Value("${app.pendencia.minutos}") long minutosDeValidade) {
        this.validador = validador;
        this.pendentes = pendentes;
        this.limites = limites;
        this.calendario = calendario;
        this.minutosDeValidade = minutosDeValidade;
    }

    @Transactional
    public String propor(BigDecimal valor, String categoriaTexto, String descricao, String dataTexto) {
        BigDecimal valorValido = validador.validarValor(valor);
        Categoria categoria = validador.validarCategoria(categoriaTexto);
        LocalDate data = validador.validarData(interpretarData(dataTexto));

        pendentes.deleteAll();
        pendentes.save(new GastoPendente(valorValido, categoria, descricao, data,
                calendario.agora().plus(minutosDeValidade, ChronoUnit.MINUTES)));

        StringBuilder fala = new StringBuilder("Vou gravar ")
                .append(Moeda.formatar(valorValido)).append(" em ").append(categoria.rotulo())
                .append(", ").append(data.equals(calendario.hoje()) ? "hoje" : "em " + DATA_FALADA.format(data))
                .append(". ");
        limites.excessoSeGravar(categoria, valorValido, data).ifPresent(excesso -> fala
                .append("Atenção: isso ultrapassa o limite de ").append(categoria.rotulo())
                .append(" em ").append(Moeda.formatar(excesso)).append(". "));
        return fala.append("Posso gravar? Diga sim ou não.").toString();
    }

    private LocalDate interpretarData(String texto) {
        if (texto == null || texto.isBlank()) {
            return calendario.hoje();
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new RegraDeGastoException("Não entendi a data. Diga, por exemplo, hoje ou ontem.");
        }
    }
}
