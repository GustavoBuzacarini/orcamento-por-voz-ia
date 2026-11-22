package br.com.orcamentovoz.suporte;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.Gasto;
import br.com.orcamentovoz.infrastructure.http.VozController;
import br.com.orcamentovoz.infrastructure.persistence.GastoPendenteRepository;
import br.com.orcamentovoz.infrastructure.persistence.GastoRepository;
import br.com.orcamentovoz.infrastructure.persistence.LimiteRepository;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Base dos testes de API: H2, relógio congelado em 2026-11-22 12:00 e IA/voz falsas. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TesteConfig.class)
public abstract class ApiTestBase {

    protected static final LocalDate HOJE = LocalDate.of(2026, 11, 22);

    @Autowired protected MockMvc mvc;
    @Autowired protected GastoRepository gastos;
    @Autowired protected GastoPendenteRepository pendentes;
    @Autowired protected LimiteRepository limites;
    @Autowired protected RelogioMutavel relogio;
    @Autowired protected AssistenteFalso assistente;
    @Autowired protected TranscritorFalso transcritor;
    @Autowired protected SintetizadorFalso sintetizador;

    @BeforeEach
    void limparTudo() {
        gastos.deleteAll();
        pendentes.deleteAll();
        limites.deleteAll();
        relogio.definir(TesteConfig.AGORA);
        assistente.falhar = false;
        transcritor.falhar = false;
        transcritor.proximaTranscricao = "";
        sintetizador.chamadas.set(0);
    }

    /** Envia um "áudio" que o transcritor falso entende como a frase dada. */
    protected MockHttpServletResponse enviarVoz(String frase) throws Exception {
        transcritor.proximaTranscricao = frase;
        return mvc.perform(multipart("/api/voz/comandos")
                        .file(new MockMultipartFile("audio", "comando.mp3", "audio/mpeg", new byte[] {1, 2, 3})))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    /** Fala uma frase e devolve o texto da resposta (cabeçalho decodificado). */
    protected String falar(String frase) throws Exception {
        return textoDaResposta(enviarVoz(frase));
    }

    protected static String textoDaResposta(MockHttpServletResponse resposta) {
        return URLDecoder.decode(resposta.getHeader(VozController.CABECALHO_TEXTO), StandardCharsets.UTF_8);
    }

    protected void gastou(String valor, Categoria categoria, LocalDate data) {
        gastos.save(new Gasto(new BigDecimal(valor), categoria, null, data, TesteConfig.AGORA));
    }
}
