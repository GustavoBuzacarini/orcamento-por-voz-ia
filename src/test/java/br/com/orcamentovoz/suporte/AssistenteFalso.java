package br.com.orcamentovoz.suporte;

import br.com.orcamentovoz.application.AssistenteFinanceiro;
import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import br.com.orcamentovoz.infrastructure.ai.FerramentasFinanceiras;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interpretador determinístico dos testes (ADR D3). Faz o papel do modelo: lê a frase, escolhe a
 * ferramenta e repassa os valores CRUS, sem validar. Quem valida é o código da aplicação.
 */
public class AssistenteFalso implements AssistenteFinanceiro {

    private static final Pattern GASTO = Pattern.compile(
            "^gastei (-?[\\d.,]+) reais? (?:no|na|em|com) (\\S+)(?: em (\\d{4}-\\d{2}-\\d{2}))?$");
    private static final Pattern LIMITE = Pattern.compile("^meu limite de (\\S+) é (\\d+) reais$");
    private static final Pattern TOTAL = Pattern.compile("^quanto gastei com (\\S+) este mês\\??$");

    private final FerramentasFinanceiras ferramentas;
    public volatile boolean falhar;

    public AssistenteFalso(FerramentasFinanceiras ferramentas) {
        this.ferramentas = ferramentas;
    }

    @Override
    public String responder(String texto) {
        if (falhar) {
            throw new AssistenteIndisponivelException(new RuntimeException("simulado"));
        }
        String frase = texto.toLowerCase().trim();
        Matcher gasto = GASTO.matcher(frase);
        if (gasto.matches()) {
            return ferramentas.proporGasto(numero(gasto.group(1)), gasto.group(2), null, gasto.group(3));
        }
        Matcher limite = LIMITE.matcher(frase);
        if (limite.matches()) {
            return ferramentas.definirLimite(limite.group(1), numero(limite.group(2)));
        }
        Matcher total = TOTAL.matcher(frase);
        if (total.matches()) {
            return ferramentas.totalPorCategoria(total.group(1));
        }
        return switch (frase) {
            case "sim" -> ferramentas.confirmarGasto();
            case "não", "nao" -> ferramentas.descartarGasto();
            case "como está meu mês?", "como está meu mês" -> ferramentas.resumoDoMes();
            default -> "Não entendi o pedido.";
        };
    }

    private static BigDecimal numero(String texto) {
        return new BigDecimal(texto.replace(',', '.'));
    }
}
