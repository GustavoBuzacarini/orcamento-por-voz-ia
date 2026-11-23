package br.com.orcamentovoz.infrastructure.ai;

import java.lang.reflect.Type;
import org.springframework.ai.tool.execution.ToolCallResultConverter;

/**
 * Devolve o texto da ferramenta como ele é. O conversor padrão do Spring AI serializa String como JSON
 * (com aspas), o que não serve para uma frase que será falada ou lida por um leitor de tela.
 */
public class TextoPuroConverter implements ToolCallResultConverter {

    @Override
    public String convert(Object resultado, Type tipoDeRetorno) {
        return resultado == null ? "" : resultado.toString();
    }
}
