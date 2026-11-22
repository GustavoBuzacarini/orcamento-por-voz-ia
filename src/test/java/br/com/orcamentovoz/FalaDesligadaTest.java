package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.orcamentovoz.suporte.ApiTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.fala.habilitada=false")
class FalaDesligadaTest extends ApiTestBase {

    @Test
    @DisplayName("@spec:AC-018 Com a fala desligada, a resposta de voz vem em texto")
    void comFalaDesligadaARespostaVemEmTexto() throws Exception {
        transcritor.proximaTranscricao = "gastei 45 reais no mercado";

        mvc.perform(multipart("/api/voz/comandos")
                        .file(new MockMultipartFile("audio", "comando.mp3", "audio/mpeg", new byte[] {1, 2, 3})))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resposta").value(org.hamcrest.Matchers.containsString("R$ 45,00 em mercado")));

        assertThat(sintetizador.chamadas.get()).isZero();
    }
}
