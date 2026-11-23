package br.com.orcamentovoz.domain;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Categorias fixas de gasto (ASM-001). */
public enum Categoria {
    MERCADO("mercado"),
    ALIMENTACAO_FORA("alimentação fora"),
    TRANSPORTE("transporte"),
    MORADIA("moradia"),
    SAUDE("saúde"),
    EDUCACAO("educação"),
    LAZER("lazer"),
    OUTROS("outros");

    private final String rotulo;

    Categoria(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    /**
     * Aceita o rótulo falado ("alimentação fora", "Saude") ou o nome do enum, sem acento e sem caixa.
     * Única tolerância extra: "outra" e "outras" valem "outros" (um modelo de linguagem concorda o gênero
     * com a palavra "categoria"). As demais categorias não têm sinônimos.
     */
    public static Optional<Categoria> deTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return Optional.empty();
        }
        String alvo = normalizar(texto);
        if (alvo.equals("outra") || alvo.equals("outras")) {
            return Optional.of(OUTROS);
        }
        return Arrays.stream(values())
                .filter(c -> normalizar(c.rotulo).equals(alvo) || normalizar(c.name()).equals(alvo))
                .findFirst();
    }

    public static String listaParaFala() {
        return Arrays.stream(values()).map(Categoria::rotulo).collect(Collectors.joining(", "));
    }

    private static String normalizar(String s) {
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).replace('_', ' ').trim();
    }
}
