package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoRelatorioDTO;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CotacaoRepository extends JpaRepository<CotacaoEntity, Integer> {

    @Query(value = """
            select new br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoRelatorioDTO (
                cot.idCotacao, 
                cot.nome, 
                cot.localDate, 
                cot.anexo, 
                cot.status, 
                cot.valor
            )
            from quotation cot 
            where (cot.idCotacao = :idCotacao or :idCotacao is null)
            """)
    List<CotacaoRelatorioDTO> listCotacoes(@Param("idCotacao") Integer idCotacao);

    @Query(value = """
            select new br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoRelatorioDTO (
                cot.idCotacao, 
                cot.nome, 
                cot.localDate, 
                cot.anexo, 
                cot.status, 
                cot.valor
            )
            from quotation cot 
            join cot.compra c
            where (c.idCompra = :idCompra)
            """)
    List<CotacaoRelatorioDTO> listCotacoesPorCompra(@Param("idCompra") Integer idCompra);
}
