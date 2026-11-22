package br.com.orcamentovoz.infrastructure.persistence;

import br.com.orcamentovoz.domain.GastoPendente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GastoPendenteRepository extends JpaRepository<GastoPendente, Long> {
}
