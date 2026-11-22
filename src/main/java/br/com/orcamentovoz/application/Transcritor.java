package br.com.orcamentovoz.application;

/** Porta de transcrição. O áudio vive só em memória (AC-015). */
public interface Transcritor {

    String transcrever(byte[] audio, String nomeDoArquivo);
}
