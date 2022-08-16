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

}
