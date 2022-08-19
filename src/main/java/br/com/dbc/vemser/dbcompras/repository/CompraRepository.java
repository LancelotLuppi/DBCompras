package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = """
                select new br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO(
                    c.idCompra, 
                    c.name, 
                    c.descricao, 
                    c.dataCompra, 
                    c.valorTotal, 
                    c.status
                )
                from purchase c 
                join c.cotacoes cot 
                where (:idCotacao is null OR cot.idCotacao = :idCotacao)
            """)
    CompraRelatorioDTO listCompraByIdCotacao(@Param("idCotacao") Integer idCotacao);
}
