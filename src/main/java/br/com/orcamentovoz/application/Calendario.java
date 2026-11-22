package br.com.orcamentovoz.application;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.stereotype.Component;

/** Datas e meses de calendário no fuso configurado (ASM-004). */
@Component
public class Calendario {

    private final Clock relogio;

    public Calendario(Clock relogio) {
        this.relogio = relogio;
    }

    public LocalDate hoje() {
        return LocalDate.now(relogio);
    }

    public Instant agora() {
        return relogio.instant();
    }

    public LocalDate inicioDoMes(LocalDate data) {
        return YearMonth.from(data).atDay(1);
    }

    public LocalDate fimDoMes(LocalDate data) {
        return YearMonth.from(data).atEndOfMonth();
    }
}
