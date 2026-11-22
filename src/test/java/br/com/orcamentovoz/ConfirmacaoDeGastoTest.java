package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.suporte.ApiTestBase;
import java.math.BigDecimal;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfirmacaoDeGastoTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-001 O gasto falado é repetido para confirmação e ainda não é gravado")
    void gastoFaladoEhRepetidoENaoGravado() throws Exception {
        String resposta = falar("gastei 45 reais no mercado");

        assertThat(resposta).contains("R$ 45,00 em mercado").contains("Posso gravar?");
        assertThat(gastos.count()).isZero();
        assertThat(pendentes.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("@spec:AC-002 Dizer sim grava o gasto")
    void dizerSimGravaOGasto() throws Exception {
        falar("gastei 45 reais no mercado");

        String resposta = falar("sim");

        assertThat(resposta).contains("gravado");
        assertThat(gastos.findAll()).singleElement().satisfies(g -> {
            assertThat(g.getValor()).isEqualByComparingTo(new BigDecimal("45.00"));
            assertThat(g.getCategoria()).isEqualTo(Categoria.MERCADO);
            assertThat(g.getData()).isEqualTo(HOJE);
        });
        assertThat(pendentes.count()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-003 Dizer não descarta o gasto pendente")
    void dizerNaoDescartaOGasto() throws Exception {
        falar("gastei 45 reais no mercado");

        String resposta = falar("não");

        assertThat(resposta).contains("descartei");
        assertThat(gastos.count()).isZero();
        assertThat(pendentes.count()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-004 Confirmar depois de muito tempo não grava nada")
    void confirmarDepoisDeMuitoTempoNaoGrava() throws Exception {
        falar("gastei 45 reais no mercado");
        relogio.avancar(Duration.ofMinutes(6));

        String resposta = falar("sim");

        assertThat(resposta).contains("Não há gasto para confirmar").contains("Fale o gasto de novo");
        assertThat(gastos.count()).isZero();
    }
}
