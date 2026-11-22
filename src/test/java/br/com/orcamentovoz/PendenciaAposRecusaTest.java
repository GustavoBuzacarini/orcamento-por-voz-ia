package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.suporte.ApiTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PendenciaAposRecusaTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-019 Uma proposta recusada cancela o gasto pendente anterior")
    void propostaRecusadaCancelaAPendenciaAnterior() throws Exception {
        assertThat(falar("gastei 45 reais no mercado")).contains("Posso gravar?");
        assertThat(pendentes.count()).isEqualTo(1);

        assertThat(falar("gastei 0 reais no mercado")).contains("maior que zero");
        assertThat(pendentes.count()).as("a pendência antiga não pode sobreviver a um erro").isZero();

        assertThat(falar("sim")).contains("Não há gasto para confirmar");
        assertThat(gastos.count()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-019 A mesma regra vale para categoria desconhecida e data futura")
    void mesmaRegraParaCategoriaEDataInvalidas() throws Exception {
        falar("gastei 45 reais no mercado");
        falar("gastei 20 reais no cassino");
        assertThat(pendentes.count()).isZero();

        falar("gastei 45 reais no mercado");
        falar("gastei 20 reais no mercado em 2026-12-01");
        assertThat(pendentes.count()).isZero();

        assertThat(falar("sim")).contains("Não há gasto para confirmar");
        assertThat(gastos.count()).isZero();
    }
}
