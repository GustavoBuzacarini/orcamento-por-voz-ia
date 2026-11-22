package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.suporte.ApiTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValidacaoDeGastoTest extends ApiTestBase {

    private void nadaFoiCriado() {
        assertThat(gastos.count()).isZero();
        assertThat(pendentes.count()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-005 Valor zero ou negativo é recusado")
    void valorZeroOuNegativoEhRecusado() throws Exception {
        assertThat(falar("gastei 0 reais no mercado")).contains("maior que zero");
        assertThat(falar("gastei -10 reais no mercado")).contains("maior que zero");
        nadaFoiCriado();
    }

    @Test
    @DisplayName("@spec:AC-006 Categoria desconhecida é recusada com as opções")
    void categoriaDesconhecidaEhRecusadaComAsOpcoes() throws Exception {
        String resposta = falar("gastei 20 reais no cassino");

        assertThat(resposta).contains("não existe").contains("mercado, alimentação fora, transporte");
        nadaFoiCriado();
    }

    @Test
    @DisplayName("@spec:AC-007 Data no futuro é recusada")
    void dataNoFuturoEhRecusada() throws Exception {
        String resposta = falar("gastei 20 reais no mercado em 2026-12-01");

        assertThat(resposta).contains("data futura");
        nadaFoiCriado();
    }

    @Test
    @DisplayName("@principle:P-003 O código decide: valores inválidos vindos do modelo nunca chegam ao banco")
    void modeloNaoConsegueGravarValorInvalido() throws Exception {
        falar("gastei -10 reais no mercado");
        falar("gastei 20 reais no cassino");
        falar("gastei 20 reais no mercado em 2026-12-01");

        // mesmo dizendo "sim", não há nada pendente para gravar
        assertThat(falar("sim")).contains("Não há gasto para confirmar");
        nadaFoiCriado();
    }
}
