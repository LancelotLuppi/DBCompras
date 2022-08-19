package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
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
                    i.valorUnitario, 
                    i.quantidade, 
                    i.valorTotal
                )
                from item i 
                join i.compra c 
                where (c.idCompra = :idCompra)
            """)
    List<ItemValorizadoDTO> listItensComValorByIdCompra(@Param("idCompra") Integer idCompra);
}
