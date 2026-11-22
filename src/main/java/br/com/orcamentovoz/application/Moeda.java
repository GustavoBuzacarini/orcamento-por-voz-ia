package br.com.orcamentovoz.application;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/** Formata valores para serem lidos em voz alta: R$ 1.250,00. */
public final class Moeda {

    private Moeda() {
    }

    public static String formatar(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.of("pt", "BR")));
        return "R$ " + formato.format(valor);
    }
}
