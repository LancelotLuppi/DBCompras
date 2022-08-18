package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotacaoXItemRepository extends JpaRepository<CotacaoXItemEntity, CotacaoXItemPK> {
}
