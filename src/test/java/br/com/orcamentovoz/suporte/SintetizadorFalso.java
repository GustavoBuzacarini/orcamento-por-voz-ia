package br.com.orcamentovoz.suporte;

import br.com.orcamentovoz.application.Sintetizador;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/** "Áudio" de teste: bytes reconhecíveis (AUDIO: + texto) para provar que a fala foi enviada. */
public class SintetizadorFalso implements Sintetizador {

    public static final String PREFIXO = "AUDIO:";

    public final AtomicInteger chamadas = new AtomicInteger();

    @Override
    public byte[] sintetizar(String texto) {
        chamadas.incrementAndGet();
        return (PREFIXO + texto).getBytes(StandardCharsets.UTF_8);
    }
}
