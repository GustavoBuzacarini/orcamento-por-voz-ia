package br.com.orcamentovoz.infrastructure.persistence;

import br.com.orcamentovoz.domain.Categoria;
import br.com.orcamentovoz.domain.LimiteCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LimiteRepository extends JpaRepository<LimiteCategoria, Categoria> {
}
