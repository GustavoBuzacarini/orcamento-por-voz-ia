package br.com.orcamentovoz.infrastructure.persistence;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.Gasto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GastoRepository extends JpaRepository<Gasto, Long> {

    @Query("select coalesce(sum(g.valor), 0) from Gasto g "
            + "where g.categoria = :categoria and g.data >= :inicio and g.data <= :fim")
    BigDecimal somaDaCategoria(@Param("categoria") Categoria categoria,
                               @Param("inicio") LocalDate inicio,
                               @Param("fim") LocalDate fim);

    List<Gasto> findByDataBetweenOrderByDataAscIdAsc(LocalDate inicio, LocalDate fim);
}
