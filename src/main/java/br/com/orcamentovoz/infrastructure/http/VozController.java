package br.com.orcamentovoz.infrastructure.http;

import br.com.orcamentovoz.application.ProcessarComandoUseCase;
import br.com.orcamentovoz.application.Sintetizador;
import br.com.orcamentovoz.application.Transcritor;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    public VozController(Transcritor transcritor, ProcessarComandoUseCase comandos, Sintetizador sintetizador) {
        this.transcritor = transcritor;
        this.comandos = comandos;
        this.sintetizador = sintetizador;
    }

    /** Recebe o áudio, devolve a resposta falada (mp3) e o mesmo texto no cabeçalho (AC-013). */
    @PostMapping(path = "/comandos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> comando(@RequestParam("audio") MultipartFile audio) throws IOException {
        if (audio.isEmpty()) {
            throw new ComandoInvalidoException("O áudio veio vazio. Grave de novo e envie.");
        }
        String transcricao = transcritor.transcrever(audio.getBytes(), audio.getOriginalFilename());
        String resposta = comandos.processar(transcricao);
        byte[] fala = sintetizador.sintetizar(resposta);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(CABECALHO_TEXTO, URLEncoder.encode(resposta, StandardCharsets.UTF_8).replace("+", "%20"))
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, CABECALHO_TEXTO)
                .body(fala);
    }
}
