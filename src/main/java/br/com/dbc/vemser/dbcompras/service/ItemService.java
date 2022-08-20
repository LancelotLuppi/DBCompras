package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final CompraServiceUtil compraServiceUtil;
    private final ObjectMapper objectMapper;

    public ItemDTO updateItem(Integer idItem, ItemCreateDTO itemAtualizadoDTO) throws EntidadeNaoEncontradaException, RegraDeNegocioException, UsuarioException {
        ItemEntity itemEntity = itemRepository.findById(idItem).orElseThrow(() -> new EntidadeNaoEncontradaException("Item inexistente"));
        CompraEntity compra = itemEntity.getCompra();
        compraServiceUtil.verificarCompraDoUserLogado(compra.getIdCompra());

        if(!compra.getStatus().equals(StatusCompra.ABERTO)){
            throw new RegraDeNegocioException("Itens de compras sem estar em aberto não podem ser atualizados!");
        }

        itemEntity.setNome(itemAtualizadoDTO.getNome());
        itemEntity.setQuantidade(itemAtualizadoDTO.getQuantidade());
        itemRepository.save(itemEntity);
        return objectMapper.convertValue(itemEntity, ItemDTO.class);
    }

}
