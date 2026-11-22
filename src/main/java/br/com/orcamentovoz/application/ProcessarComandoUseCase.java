package br.com.orcamentovoz.application;

import org.springframework.stereotype.Service;

@Service
public class ProcessarComandoUseCase {

    public static final String NAO_ENTENDI = "Não entendi, pode repetir?";

    private final AssistenteFinanceiro assistente;

    public ProcessarComandoUseCase(AssistenteFinanceiro assistente) {
        this.assistente = assistente;
    }

    /** Texto vazio nunca chega ao modelo e nunca cria nada (AC-014). */
    public String processar(String texto) {
        if (texto == null || texto.isBlank()) {
            return NAO_ENTENDI;
        }
        return assistente.responder(texto.trim());
    }
}
