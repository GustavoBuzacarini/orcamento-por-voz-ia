package br.com.orcamentovoz;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.orcamentovoz.application.ProcessarComandoUseCase;
import br.com.orcamentovoz.suporte.ApiTestBase;
import br.com.orcamentovoz.suporte.SintetizadorFalso;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;

class AcessibilidadeTest extends ApiTestBase {

    @Autowired EntityManager em;

    @Test
    @DisplayName("@spec:AC-013 Toda resposta de voz volta em áudio e com o texto equivalente")
    void respostaDeVozVoltaEmAudioEComTexto() throws Exception {
        MockHttpServletResponse resposta = enviarVoz("gastei 45 reais no mercado");

        String texto = textoDaResposta(resposta);
        assertThat(resposta.getContentType()).isEqualTo("audio/mpeg");
        assertThat(resposta.getContentAsByteArray())
                .isEqualTo((SintetizadorFalso.PREFIXO + texto).getBytes(StandardCharsets.UTF_8));
        assertThat(texto).contains("R$ 45,00 em mercado");
    }

    @Test
    @DisplayName("@spec:AC-014 Áudio que não foi entendido pede para repetir")
    void audioNaoEntendidoPedeParaRepetir() throws Exception {
        MockHttpServletResponse resposta = enviarVoz("");

        assertThat(textoDaResposta(resposta)).isEqualTo(ProcessarComandoUseCase.NAO_ENTENDI);
        assertThat(gastos.count()).isZero();
        assertThat(pendentes.count()).isZero();
        assertThat(limites.count()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-017 O mesmo comando pode ser enviado por texto")
    void mesmoComandoPodeSerEnviadoPorTexto() throws Exception {
        mvc.perform(post("/api/texto/comandos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"texto\":\"gastei 45 reais no mercado\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resposta").value(org.hamcrest.Matchers.containsString("R$ 45,00 em mercado")));

        assertThat(sintetizador.chamadas.get()).isZero();
    }

    @Test
    @DisplayName("@spec:AC-015 O áudio não fica guardado")
    void audioNaoFicaGuardado() throws Exception {
        Path tmp = Path.of(System.getProperty("java.io.tmpdir"));
        Set<String> antes = arquivosEm(tmp);

        enviarVoz("gastei 45 reais no mercado");

        assertThat(arquivosEm(tmp)).isEqualTo(antes);
        for (EntityType<?> entidade : em.getMetamodel().getEntities()) {
            for (Attribute<?, ?> atributo : entidade.getAttributes()) {
                assertThat(atributo.getJavaType())
                        .as("%s.%s não pode guardar bytes de áudio", entidade.getName(), atributo.getName())
                        .isNotEqualTo(byte[].class);
            }
        }
    }

    @Test
    @DisplayName("@spec:AC-016 Falha da IA devolve aviso claro e não grava nada")
    void falhaDaIaDevolveAvisoENaoGrava() throws Exception {
        transcritor.falhar = true;
        mvc.perform(multipart("/api/voz/comandos")
                        .file(new MockMultipartFile("audio", "comando.mp3", "audio/mpeg", new byte[] {1, 2, 3})))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensagem").value("o assistente está indisponível, tente de novo em instantes"));

        transcritor.falhar = false;
        assistente.falhar = true;
        mvc.perform(post("/api/texto/comandos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"texto\":\"gastei 45 reais no mercado\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.mensagem").value("o assistente está indisponível, tente de novo em instantes"));

        assertThat(gastos.count()).isZero();
        assertThat(pendentes.count()).isZero();
        assertThat(limites.count()).isZero();
    }

    private static Set<String> arquivosEm(Path pasta) throws IOException {
        try (Stream<Path> arquivos = Files.list(pasta)) {
            return arquivos.map(p -> p.getFileName().toString()).collect(Collectors.toSet());
        }
    }
}
