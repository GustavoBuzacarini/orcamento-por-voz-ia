package br.com.orcamentovoz.application;

/** A IA (transcrição, chat ou fala) falhou. Nada deve ter sido gravado (AC-016). */
public class AssistenteIndisponivelException extends RuntimeException {

    public AssistenteIndisponivelException(Throwable causa) {
        super("o assistente está indisponível, tente de novo em instantes", causa);
    }
}
