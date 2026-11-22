package br.com.orcamentovoz.suporte;

import br.com.orcamentovoz.infrastructure.ai.FerramentasFinanceiras;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/** Troca relógio, IA e voz por versões controláveis. Nenhum teste chama a OpenAI. */
@TestConfiguration
public class TesteConfig {

    public static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    /** 2026-11-22 12:00 em Brasília. */
    public static final Instant AGORA = Instant.parse("2026-11-22T15:00:00Z");

    @Bean
    @Primary
    public RelogioMutavel relogioDeTeste() {
        return new RelogioMutavel(AGORA, FUSO);
    }

    @Bean
    @Primary
    public AssistenteFalso assistenteFalso(FerramentasFinanceiras ferramentas) {
        return new AssistenteFalso(ferramentas);
    }

    @Bean
    @Primary
    public TranscritorFalso transcritorFalso() {
        return new TranscritorFalso();
    }

    @Bean
    @Primary
    public SintetizadorFalso sintetizadorFalso() {
        return new SintetizadorFalso();
    }
}
