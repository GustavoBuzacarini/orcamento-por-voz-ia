package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.suporte.ApiTestBase;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConsultasTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-011 Total por categoria no mês")
    void totalPorCategoriaNoMes() throws Exception {
        gastou("40.00", Categoria.MERCADO, HOJE);
        gastou("60.00", Categoria.MERCADO, LocalDate.of(2026, 11, 3));
        gastou("30.00", Categoria.LAZER, HOJE);
        gastou("500.00", Categoria.MERCADO, LocalDate.of(2026, 10, 15));

        String resposta = falar("quanto gastei com mercado este mês?");

        assertThat(resposta).contains("R$ 100,00").doesNotContain("R$ 130,00").doesNotContain("R$ 600,00");
    }

    @Test
    @DisplayName("@spec:AC-012 Resumo do mês com categorias acima do limite")
    void resumoDoMesComCategoriasAcimaDoLimite() throws Exception {
        gastou("100.00", Categoria.MERCADO, HOJE);
        gastou("330.00", Categoria.LAZER, HOJE);
        falar("meu limite de lazer é 300 reais");

        String resposta = falar("como está meu mês?");

        assertThat(resposta).contains("R$ 430,00").contains("Mercado: R$ 100,00").contains("Lazer: R$ 330,00")
                .contains("Acima do limite: lazer");
    }
}
