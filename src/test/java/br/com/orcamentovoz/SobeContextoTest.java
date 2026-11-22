package br.com.orcamentovoz;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SobeContextoTest {

    @Test
    @DisplayName("Esqueleto: a aplicação sobe com Spring AI 2.0.1 e banco de teste")
    void contextoSobe() {
    }
}
