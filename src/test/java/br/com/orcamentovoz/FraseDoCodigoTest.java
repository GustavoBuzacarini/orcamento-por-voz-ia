package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.orcamentovoz.application.Calendario;
import br.com.orcamentovoz.infrastructure.ai.ChatClientAssistente;
import br.com.orcamentovoz.infrastructure.ai.FerramentasFinanceiras;
import br.com.orcamentovoz.suporte.ApiTestBase;
import br.com.orcamentovoz.suporte.ChatModelFalso;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/** Usa o ChatClientAssistente de verdade (Spring AI), trocando só o modelo de linguagem por um falso. */
class FraseDoCodigoTest extends ApiTestBase {

    @Autowired FerramentasFinanceiras ferramentas;
    @Autowired Calendario calendario;

    private ChatClientAssistente assistenteCom(ChatModelFalso modelo) {
        return new ChatClientAssistente(ChatClient.builder(modelo), ferramentas, calendario,
                new ClassPathResource("prompts/assistente.st"), "modelo-de-teste", false);
    }

    @Test
    @DisplayName("@spec:AC-020 A frase de confirmação é montada pelo código, não pela IA")
    void fraseDeConfirmacaoVemDoCodigo() {
        ChatModelFalso modelo = new ChatModelFalso("proporGasto", "{\"valor\":45,\"categoria\":\"mercado\"}");

        String fala = assistenteCom(modelo).responder("gastei 45 reais no mercado");

        assertThat(fala).isEqualTo("Vou gravar R$ 45,00 em mercado, hoje. Posso gravar? Diga sim ou não.");
        assertThat(modelo.chamadas).as("uma só chamada à IA por comando").isEqualTo(1);
        assertThat(pendentes.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("@spec:AC-020 Erros de validação também chegam à pessoa com o texto do código")
    void erroDeValidacaoVemDoCodigo() {
        ChatModelFalso modelo = new ChatModelFalso("proporGasto", "{\"valor\":0,\"categoria\":\"mercado\"}");

        String fala = assistenteCom(modelo).responder("gastei 0 reais no mercado");

        assertThat(fala).isEqualTo("O valor precisa ser maior que zero.");
        assertThat(modelo.chamadas).isEqualTo(1);
        assertThat(pendentes.count()).isZero();
        assertThat(gastos.count()).isZero();
    }
}
