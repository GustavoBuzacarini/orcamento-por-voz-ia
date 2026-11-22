package br.com.orcamentovoz.suporte;

import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import br.com.orcamentovoz.application.Transcritor;

/** Devolve a frase que o teste configurou, como se o áudio tivesse sido dito assim. */
public class TranscritorFalso implements Transcritor {

    public volatile String proximaTranscricao = "";
    public volatile boolean falhar;

    @Override
    public String transcrever(byte[] audio, String nomeDoArquivo) {
        if (falhar) {
            throw new AssistenteIndisponivelException(new RuntimeException("simulado"));
        }
        return proximaTranscricao;
    }
}
