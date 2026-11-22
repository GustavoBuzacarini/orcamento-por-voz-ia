package br.com.orcamentovoz.infrastructure.ai;

import br.com.orcamentovoz.application.ConfirmarGastoUseCase;
import br.com.orcamentovoz.application.ConsultarGastosUseCase;
import br.com.orcamentovoz.application.DefinirLimiteUseCase;
import br.com.orcamentovoz.application.RegistrarGastoUseCase;
import br.com.orcamentovoz.application.ResumoMesUseCase;
import br.com.orcamentovoz.domain.RegraDeGastoException;
import java.math.BigDecimal;
import java.util.function.Supplier;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * Ferramentas que o modelo pode chamar (Tool Calling). São finas de propósito: não decidem nada,
 * só repassam aos casos de uso (P-003). Quando uma regra é violada, devolvem a mensagem para o
 * modelo falar à pessoa.
 */
@Component
public class FerramentasFinanceiras {

    private final RegistrarGastoUseCase registrar;
    private final ConfirmarGastoUseCase confirmar;
    private final DefinirLimiteUseCase definirLimite;
    private final ConsultarGastosUseCase consultar;
    private final ResumoMesUseCase resumo;

    public FerramentasFinanceiras(RegistrarGastoUseCase registrar, ConfirmarGastoUseCase confirmar,
                                  DefinirLimiteUseCase definirLimite, ConsultarGastosUseCase consultar,
                                  ResumoMesUseCase resumo) {
        this.registrar = registrar;
        this.confirmar = confirmar;
        this.definirLimite = definirLimite;
        this.consultar = consultar;
        this.resumo = resumo;
    }

    @Tool(description = "Propõe um gasto para confirmação. NÃO grava. Use quando a pessoa disser que gastou, "
            + "pagou ou comprou algo. Depois, leia a resposta e pergunte se pode gravar.")
    public String proporGasto(
            @ToolParam(description = "Valor em reais, por exemplo 45.50") BigDecimal valor,
            @ToolParam(description = "Categoria: mercado, alimentação fora, transporte, moradia, saúde, educação, lazer ou outros") String categoria,
            @ToolParam(required = false, description = "Descrição curta, se a pessoa disser") String descricao,
            @ToolParam(required = false, description = "Data no formato AAAA-MM-DD; omita para hoje") String data) {
        return executar(() -> registrar.propor(valor, categoria, descricao, data));
    }

    @Tool(description = "Grava o gasto pendente. Use SOMENTE quando a pessoa disser sim ou confirmar.")
    public String confirmarGasto() {
        return executar(confirmar::confirmar);
    }

    @Tool(description = "Descarta o gasto pendente. Use quando a pessoa disser não, cancelar ou esquecer.")
    public String descartarGasto() {
        return executar(confirmar::descartar);
    }

    @Tool(description = "Define o limite mensal de uma categoria, substituindo o anterior.")
    public String definirLimite(
            @ToolParam(description = "Categoria do limite") String categoria,
            @ToolParam(description = "Valor mensal em reais") BigDecimal valor) {
        return executar(() -> definirLimite.definir(categoria, valor));
    }

    @Tool(description = "Informa quanto a pessoa gastou em uma categoria neste mês.")
    public String totalPorCategoria(@ToolParam(description = "Categoria consultada") String categoria) {
        return executar(() -> consultar.totalPorCategoria(categoria));
    }

    @Tool(description = "Resume o mês: total, total por categoria e categorias acima do limite.")
    public String resumoDoMes() {
        return executar(resumo::resumo);
    }

    private String executar(Supplier<String> acao) {
        try {
            return acao.get();
        } catch (RegraDeGastoException e) {
            return e.getMessage();
        }
    }
}
