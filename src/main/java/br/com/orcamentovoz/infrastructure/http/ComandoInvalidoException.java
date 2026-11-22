package br.com.orcamentovoz.infrastructure.http;

/** Requisição que o cliente precisa corrigir (por exemplo, áudio vazio): vira 400. */
public class ComandoInvalidoException extends RuntimeException {

    public ComandoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
