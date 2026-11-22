package br.com.orcamentovoz.infrastructure.http;

import br.com.orcamentovoz.application.ProcessarComandoUseCase;
import br.com.orcamentovoz.application.Sintetizador;
import br.com.orcamentovoz.application.Transcritor;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voz")
public class VozController {

    public static final String CABECALHO_TEXTO = "X-Resposta-Texto";

    private final Transcritor transcritor;
    private final ProcessarComandoUseCase comandos;
    private final Sintetizador sintetizador;
    private final boolean falaHabilitada;

    public VozController(Transcritor transcritor, ProcessarComandoUseCase comandos, Sintetizador sintetizador,
                         @Value("${app.fala.habilitada:true}") boolean falaHabilitada) {
        this.transcritor = transcritor;
        this.comandos = comandos;
        this.sintetizador = sintetizador;
        this.falaHabilitada = falaHabilitada;
    }

    /**
     * Recebe o áudio. Com a fala ligada, devolve a resposta falada (mp3) e o mesmo texto no cabeçalho
     * (AC-013). Com a fala desligada, devolve só o texto em JSON (AC-018).
     */
    @PostMapping(path = "/comandos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> comando(@RequestParam("audio") MultipartFile audio) throws IOException {
        if (audio.isEmpty()) {
            throw new ComandoInvalidoException("O áudio veio vazio. Grave de novo e envie.");
        }
        String transcricao = transcritor.transcrever(audio.getBytes(), audio.getOriginalFilename());
        String resposta = comandos.processar(transcricao);
        String textoNoCabecalho = URLEncoder.encode(resposta, StandardCharsets.UTF_8).replace("+", "%20");

        if (!falaHabilitada) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(CABECALHO_TEXTO, textoNoCabecalho)
                    .body(Map.of("resposta", resposta));
        }
        byte[] fala = sintetizador.sintetizar(resposta);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(CABECALHO_TEXTO, textoNoCabecalho)
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, CABECALHO_TEXTO)
                .body(fala);
    }
}
