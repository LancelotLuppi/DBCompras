package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRepository extends JpaRepository<CotacaoEntity, Integer> {

    @Query("select q " +
            "from quotation q " +
            "where id_user = :idUser")
    List<CotacaoEntity> findByUsuario(Integer idUser);

    @Query("insert into quotation (valor)" +
            "  select i.amount, qi.value " +
            "  from item i" +
            "  inner join quotation_item qi" +
            "  on i.id_item = qi.id_item" +
            "values(:valorTotal)")
    Double valorDaCotacao(Double valorTotal);

}
