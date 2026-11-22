package br.com.orcamentovoz.infrastructure.ai;

import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import br.com.orcamentovoz.application.Transcritor;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

/** Transcrição via Spring AI. O áudio fica só em memória: nenhum arquivo temporário (AC-015). */
@Component
public class TranscricaoOpenAi implements Transcritor {

    private final TranscriptionModel modelo;

    public TranscricaoOpenAi(TranscriptionModel modelo) {
        this.modelo = modelo;
    }

    @Override
    public String transcrever(byte[] audio, String nomeDoArquivo) {
        String nome = nomeDoArquivo == null || nomeDoArquivo.isBlank() ? "audio.mp3" : nomeDoArquivo;
        ByteArrayResource recurso = new ByteArrayResource(audio) {
            @Override
            public String getFilename() {
                return nome;
            }
        };
        try {
            return modelo.transcribe(recurso);
        } catch (RuntimeException e) {
            throw new AssistenteIndisponivelException(e);
        }
    }
}
