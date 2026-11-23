package br.com.orcamentovoz.suporte;

import java.util.List;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.AssistantMessage.ToolCall;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

/**
 * Modelo de linguagem falso: na primeira chamada escolhe uma ferramenta; se for chamado uma segunda vez,
 * "reescreve" a frase. Serve para provar que o sistema não depende da redação do modelo (AC-020).
 */
public class ChatModelFalso implements ChatModel {

    public static final String FRASE_DO_MODELO = "A IA reescreveu a frase.";

    private final String ferramenta;
    private final String argumentosJson;
    public int chamadas;

    public ChatModelFalso(String ferramenta, String argumentosJson) {
        this.ferramenta = ferramenta;
        this.argumentosJson = argumentosJson;
    }

    /** O advisor de ferramentas do Spring AI 2.0 exige opções que suportem ferramentas. */
    @Override
    public ChatOptions getOptions() {
        return ToolCallingChatOptions.builder().build();
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return ToolCallingChatOptions.builder().build();
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        chamadas++;
        AssistantMessage mensagem = chamadas == 1
                ? AssistantMessage.builder().content("")
                        .toolCalls(List.of(new ToolCall("chamada-1", "function", ferramenta, argumentosJson))).build()
                : AssistantMessage.builder().content(FRASE_DO_MODELO).build();
        ChatGenerationMetadata metadados = ChatGenerationMetadata.builder()
                .finishReason(chamadas == 1 ? "tool_calls" : "stop").build();
        return new ChatResponse(List.of(new Generation(mensagem, metadados)));
    }
}
