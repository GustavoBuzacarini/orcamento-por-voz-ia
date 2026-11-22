package br.com.orcamentovoz.infrastructure.ai;

import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import br.com.orcamentovoz.application.Sintetizador;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.stereotype.Component;

@Component
public class FalaOpenAi implements Sintetizador {

    private final TextToSpeechModel modelo;

    public FalaOpenAi(TextToSpeechModel modelo) {
        this.modelo = modelo;
    }

    @Override
    public byte[] sintetizar(String texto) {
        try {
            return modelo.call(texto);
        } catch (RuntimeException e) {
            throw new AssistenteIndisponivelException(e);
        }
    }
}
