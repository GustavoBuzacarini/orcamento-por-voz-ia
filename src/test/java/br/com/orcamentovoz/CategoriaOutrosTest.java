package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.suporte.ApiTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoriaOutrosTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-021 Outra e outras são aceitos como a categoria outros")
    void outraEOutrasSaoAceitasComoOutros() throws Exception {
        assertThat(falar("gastei 10 reais em outras"))
                .isEqualTo("Vou gravar R$ 10,00 em outros, hoje. Posso gravar? Diga sim ou não.");
        assertThat(falar("gastei 10 reais em outra"))
                .isEqualTo("Vou gravar R$ 10,00 em outros, hoje. Posso gravar? Diga sim ou não.");

        assertThat(Categoria.deTexto("Outras")).contains(Categoria.OUTROS);
        assertThat(Categoria.deTexto("OUTRA")).contains(Categoria.OUTROS);
        assertThat(Categoria.deTexto("outros")).contains(Categoria.OUTROS);
    }

    @Test
    @DisplayName("@spec:AC-021 Categorias que não existem continuam recusadas")
    void categoriasInexistentesContinuamRecusadas() throws Exception {
        assertThat(Categoria.deTexto("cassino")).isEmpty();
        assertThat(Categoria.deTexto("outrem")).isEmpty();
        assertThat(falar("gastei 10 reais no cassino")).contains("não existe");
        assertThat(pendentes.count()).isZero();
    }
}
