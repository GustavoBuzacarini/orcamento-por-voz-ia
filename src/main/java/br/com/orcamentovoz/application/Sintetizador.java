package br.com.orcamentovoz.application;

/** Porta de fala: texto vira áudio mp3. */
public interface Sintetizador {

    byte[] sintetizar(String texto);
}
