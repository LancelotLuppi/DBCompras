package br.com.dbc.vemser.dbcompras.repository;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraListDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<CompraEntity, Integer> {

    @Query("SELECT new br.com.dbc.vemser.dbcompras.dto.compra.CompraListDTO(c.idCompra, c.name, c.dataCompra, c.valor, c.cotacoes, c.status) " +
            "FROM compra c WHERE LOWER(c.name) like lower()")
    public CompraListDTO listComprasParaGestor(String nome, String email);
}
