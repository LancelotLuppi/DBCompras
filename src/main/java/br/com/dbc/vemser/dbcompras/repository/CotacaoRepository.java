package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotacaoRepository extends JpaRepository<CotacaoEntity, Integer> {
}
