package br.com.orcamentovoz.infrastructure.ai;

import br.com.orcamentovoz.application.AssistenteFinanceiro;
import br.com.orcamentovoz.application.AssistenteIndisponivelException;
import br.com.orcamentovoz.application.Calendario;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/** Implementação real da porta: ChatClient do Spring AI com as ferramentas registradas. */
@Component
public class ChatClientAssistente implements AssistenteFinanceiro {

    private final ChatClient chatClient;
    private final Calendario calendario;

    public ChatClientAssistente(ChatClient.Builder builder, FerramentasFinanceiras ferramentas,
                                Calendario calendario,
                                @Value("classpath:/prompts/assistente.st") Resource instrucoes,
                                @Value("${spring.ai.openai.chat.model}") String modelo,
                                @Value("${app.ia.sem-raciocinio:false}") boolean semRaciocinio) {
        builder.defaultSystem(instrucoes).defaultTools(ferramentas);
        if (semRaciocinio) {
            // Groq: modelos de raciocínio devolvem reasoning_content, e a Groq recusa esse campo quando o
            // Spring AI reenvia a mensagem depois da ferramenta (erro 400). Pedir sem raciocínio evita isso.
            builder.defaultOptions(OpenAiChatOptions.builder()
                    .model(modelo)
                    .extraBody(Map.of("include_reasoning", false)));
        }
        this.chatClient = builder.build();
        this.calendario = calendario;
    }

    @Override
    public String responder(String texto) {
        try {
            String conteudo = chatClient.prompt()
                    .user("Data de hoje: " + calendario.hoje() + ". Pessoa disse: " + texto)
                    .call()
                    .content();
            return conteudo == null ? "" : conteudo;
        } catch (RuntimeException e) {
            throw new AssistenteIndisponivelException(e);
        }
    }
}
