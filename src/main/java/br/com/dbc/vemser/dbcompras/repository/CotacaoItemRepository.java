package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.entity.CotacaoItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoItemPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CotacaoItemRepository extends JpaRepository<CotacaoItemEntity, CotacaoItemPk> {
}
