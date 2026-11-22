package br.com.orcamentovoz.infrastructure.http;

import br.com.orcamentovoz.application.ProcessarComandoUseCase;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Mesmo fluxo da voz, mas entrando e saindo como texto: para testar sem microfone e para leitores de tela. */
@RestController
@RequestMapping("/api/texto")
public class TextoController {

    public record Comando(@NotBlank String texto) {
    }

    private final ProcessarComandoUseCase comandos;

    public TextoController(ProcessarComandoUseCase comandos) {
        this.comandos = comandos;
    }

    @PostMapping("/comandos")
    public Map<String, String> comando(@RequestBody @jakarta.validation.Valid Comando comando) {
        return Map.of("resposta", comandos.processar(comando.texto()));
    }
}
