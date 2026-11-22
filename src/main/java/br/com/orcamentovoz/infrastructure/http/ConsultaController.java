package br.com.orcamentovoz.infrastructure.http;

import br.com.orcamentovoz.application.ResumoMesUseCase;
import br.com.orcamentovoz.infrastructure.persistence.GastoRepository;
import br.com.orcamentovoz.infrastructure.persistence.LimiteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Leitura em JSON para conferir o que foi gravado. */
@RestController
@RequestMapping("/api")
public class ConsultaController {

    public record GastoDto(Long id, BigDecimal valor, String categoria, String descricao, LocalDate data) {
    }

    public record LimiteDto(String categoria, BigDecimal valorMensal) {
    }

    private final GastoRepository gastos;
    private final LimiteRepository limites;
    private final ResumoMesUseCase resumo;

    public ConsultaController(GastoRepository gastos, LimiteRepository limites, ResumoMesUseCase resumo) {
        this.gastos = gastos;
        this.limites = limites;
        this.resumo = resumo;
    }

    @GetMapping("/gastos")
    public List<GastoDto> gastos() {
        return gastos.findAll(Sort.by("data", "id")).stream()
                .map(g -> new GastoDto(g.getId(), g.getValor(), g.getCategoria().rotulo(), g.getDescricao(), g.getData()))
                .toList();
    }

    @GetMapping("/limites")
    public List<LimiteDto> limites() {
        return limites.findAll().stream()
                .map(l -> new LimiteDto(l.getCategoria().rotulo(), l.getValorMensal()))
                .toList();
    }

    @GetMapping("/resumo")
    public Map<String, String> resumo() {
        return Map.of("resumo", resumo.resumo());
    }
}
