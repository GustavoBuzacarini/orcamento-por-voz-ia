package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.suporte.ApiTestBase;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LimitesTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-008 Definir um limite mensal por voz")
    void definirLimiteMensalPorVoz() throws Exception {
        assertThat(falar("meu limite de lazer é 300 reais")).contains("Limite de lazer: R$ 300,00 por mês.");
        assertThat(limites.findAll()).singleElement()
                .satisfies(l -> assertThat(l.getValorMensal()).isEqualByComparingTo(new BigDecimal("300")));

        falar("meu limite de lazer é 400 reais");

        assertThat(limites.findAll()).singleElement()
                .satisfies(l -> assertThat(l.getValorMensal()).isEqualByComparingTo(new BigDecimal("400")));
    }

    @Test
    @DisplayName("@spec:AC-009 Gasto que ultrapassa o limite é gravado e a pessoa é avisada")
    void gastoQueUltrapassaOLimiteEhGravadoEAvisa() throws Exception {
        falar("meu limite de lazer é 300 reais");
        gastou("280.00", Categoria.LAZER, HOJE);

        assertThat(falar("gastei 50 reais em lazer")).contains("ultrapassa o limite de lazer em R$ 30,00");
        String resposta = falar("sim");

        assertThat(resposta).contains("gravado").contains("o limite de lazer foi ultrapassado em R$ 30,00");
        assertThat(gastos.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("@spec:AC-010 Gasto dentro do limite informa quanto ainda resta")
    void gastoDentroDoLimiteInformaQuantoResta() throws Exception {
        falar("meu limite de lazer é 300 reais");
        gastou("100.00", Categoria.LAZER, HOJE);

        falar("gastei 50 reais em lazer");
        String resposta = falar("sim");

        assertThat(resposta).contains("gravado").contains("Ainda restam R$ 150,00 no limite de lazer")
                .doesNotContain("ultrapassado");
        assertThat(gastos.count()).isEqualTo(2);
    }
}
