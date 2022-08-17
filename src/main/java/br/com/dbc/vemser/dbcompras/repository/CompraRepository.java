package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<CompraEntity, Integer> {

    @Modifying
    @Query("delete " +
            "from purchase p " +
            "where p.idCompra = :idCompra")
    @Transactional
    Integer deleteCompra(Integer idCompra);

    @Query("select p " +
            "from purchase p " +
            "join p.usuario u " +
            "where (u.idUser = :idUsuario)")
    List<CompraEntity> findAllByUsuarioId(Integer idUsuario);
}
