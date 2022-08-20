package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraListCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
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
    void deleteCompra(Integer idCompra);

    @Query("select p " +
            "from purchase p " +
            "join p.usuario u " +
            "where (u.idUser = :idUsuario)")
    List<CompraEntity> findAllByUsuarioId(Integer idUsuario);

    @Query(value = """
                select new br.com.dbc.vemser.dbcompras.dto.compra.CompraListCotacaoDTO(
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
    CompraListCotacaoDTO listCompraByIdCotacao(@Param("idCotacao") Integer idCotacao);

    @Query("select new br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO (" +
            "p.idCompra," +
            "p.name," +
            "p.descricao," +
            "p.dataCompra," +
            "p.valorTotal," +
            "p.status," +
            "u.idUser," +
            "u.nome" +
            ")" +
            " from purchase p " +
            "left join p.usuario u" +
            " where (:idCompra is null OR p.idCompra = :idCompra)")
    List<CompraRelatorioDTO> findByCompraId(Integer idCompra);


//    @Query("""
//            select c
//            from purchase c
//            join c.usuario u
//            where (u.nome LIKE CONCAT('%',:nomeUsuario,'%'))
//            ORDER BY CASE c.status LIKE CONCAT('%',APROVADO_GESTOR,'%'))
//            """)
//    List<CompraEntity> listByNomeUsuarioAndCompraNome(@Param("nomeUsuario") String nomeUsuario, @Param("nomeCompra") String nomeCompra);

    @Query("""
            select c
            from purchase c
            where (c.name LIKE CONCAT('%',:nomeCompra, '%'))
            order by case LIKE CONCAT('%', APROVADO_GESTOR ,'%')
            """)
    List<CompraEntity> listByNomeCompra(@Param("nomeCompra") String nomeCompra);
}
