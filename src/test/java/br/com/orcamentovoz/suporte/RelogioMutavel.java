package br.com.orcamentovoz.suporte;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Relógio de teste que pode ser reposicionado e avançado. */
public class RelogioMutavel extends Clock {

    private final ZoneId fuso;
    private volatile Instant agora;

    public RelogioMutavel(Instant inicial, ZoneId fuso) {
        this.agora = inicial;
        this.fuso = fuso;
    }

    public void definir(Instant instante) {
        this.agora = instante;
    }

    public void avancar(Duration duracao) {
        this.agora = agora.plus(duracao);
    }

    @Override
    public ZoneId getZone() {
        return fuso;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new RelogioMutavel(agora, zone);
    }

    @Override
    public Instant instant() {
        return agora;
    }
}
