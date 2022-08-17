package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompraUtil {
    private final UsuarioUtil usuarioUtil;
    private final ObjectMapper objectMapper;
    private final ItemRepository itemRepository;


    public CompraEntity findByID(Integer idCompra) throws UsuarioException, EntidadeNaoEncontradaException {
        UsuarioEntity usuario = usuarioUtil.retornarUsuarioEntityLogado();

        Set<CompraEntity> compras = usuario.getCompras();

        return compras.stream()
                .filter(compraEntity -> compraEntity.getIdCompra().equals(idCompra))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Esta não compra não existe"));
    }

    public void verificarCompraDoUserLogado(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = usuarioUtil.retornarUsuarioEntityLogado();
        List<Integer> idCompras = usuario.getCompras().stream()
                .map(CompraEntity::getIdCompra).toList();
        if (!idCompras.contains(idCompra)) {
            throw new RegraDeNegocioException("Esta compra não é sua!");
        }
    }

    public Set<ItemEntity> salvarItensDaCompra(CompraCreateDTO compraCreateDTO, CompraEntity compraSalva) {
        return compraCreateDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .peek(itemEntity -> itemEntity.setCompra(compraSalva))
                .map(itemRepository::save)
                .collect(Collectors.toSet());
    }

    public CompraDTO converterCompraEntityToCompraDTO(CompraEntity compra) {
        List<ItemDTO> itemDTOList = compra.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                .toList();
        CompraDTO compraDTO = objectMapper.convertValue(compra, CompraDTO.class);
        compraDTO.setItens(itemDTOList);
        return compraDTO;
    }
}