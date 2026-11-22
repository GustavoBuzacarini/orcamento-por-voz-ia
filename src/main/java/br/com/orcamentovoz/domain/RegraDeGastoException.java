package br.com.orcamentovoz.domain;

/** Violação de regra de negócio. A mensagem é escrita para ser falada à pessoa usuária. */
public class RegraDeGastoException extends RuntimeException {

    public RegraDeGastoException(String mensagem) {
        super(mensagem);
    }
}
