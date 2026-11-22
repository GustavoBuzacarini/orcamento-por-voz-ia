package br.com.orcamentovoz.infrastructure.http;

import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ErrosHandler {

    private static final Logger log = LoggerFactory.getLogger(ErrosHandler.class);

    @ExceptionHandler(AssistenteIndisponivelException.class)
    public ResponseEntity<Map<String, String>> iaIndisponivel(AssistenteIndisponivelException e) {
        log.warn("Falha no serviço de IA: {}", e.getCause() == null ? e.getMessage() : e.getCause().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("mensagem", e.getMessage()));
    }

    @ExceptionHandler(ComandoInvalidoException.class)
    public ResponseEntity<Map<String, String>> invalido(ComandoInvalidoException e) {
        return ResponseEntity.badRequest().body(Map.of("mensagem", e.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> grande(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE)
                .body(Map.of("mensagem", "O áudio é grande demais. O limite é 5 MB."));
    }
}
