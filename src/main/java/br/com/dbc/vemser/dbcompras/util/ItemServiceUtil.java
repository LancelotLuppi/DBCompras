package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ItemServiceUtil {

    public void verificarItensDaCompra(CompraEntity compra, List<Integer> idItensRecebido) throws RegraDeNegocioException {
        List<Integer> idItensDaCompra = compra.getItens().stream()
                .map(ItemEntity::getIdItem)
                .toList();

        List<Integer> idsIguais = idItensDaCompra.stream()
                .filter(id -> idItensRecebido.contains(id))
                .toList();

        if(idsIguais.size() != idItensRecebido.size()) {
            throw new RegraDeNegocioException("Um desses itens não é seu!");
        }
    }
}
