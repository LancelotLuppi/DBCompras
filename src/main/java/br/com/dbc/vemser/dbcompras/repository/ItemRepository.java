package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    @Query(value = """
                select new br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO(
                    i.idItem, 
                    i.nome, 
                    iXcot.valorDoItem, 
                    i.quantidade, 
                    iXcot.valorTotal
                )
                from item i
                join i.cotacoes iXcot 
                join i.compra c
                join c.cotacoes cot 
                where (c.idCompra = :idCompra AND cot.idCotacao = :idCotacao)
            """)
    List<ItemValorizadoDTO> findItemComValorByIdCompraAndIdCotacao(@Param("idCompra") Integer idCompra, @Param("idCotacao") Integer idCotacao);

    @Query(value = """
                select new br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO(
                    i.idItem, 
                    i.nome, 
                    iXcot.valorDoItem, 
                    i.quantidade, 
                    iXcot.valorTotal
                )
                from item i
                join i.cotacoes iXcot 
                where (iXcot.cotacaoXItemPK = :pk)
            """)
    ItemValorizadoDTO findItemComValorByIdCompraAndIdCotacao(@Param("pk") CotacaoXItemPK pk);
}
