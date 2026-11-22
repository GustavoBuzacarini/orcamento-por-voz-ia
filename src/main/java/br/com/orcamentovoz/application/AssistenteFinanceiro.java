package br.com.orcamentovoz.application;

/** Porta da IA: entende o texto, escolhe uma ferramenta e devolve a frase a ser falada (ADR D3). */
public interface AssistenteFinanceiro {

    String responder(String texto);
}
