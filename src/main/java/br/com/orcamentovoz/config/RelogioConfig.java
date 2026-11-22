package br.com.orcamentovoz.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RelogioConfig {

    /** Relógio único da aplicação (ASM-004: fuso America/Sao_Paulo). Os testes o substituem. */
    @Bean
    public Clock relogio(@Value("${app.fuso}") String fuso) {
        return Clock.system(ZoneId.of(fuso));
    }
}
