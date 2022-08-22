package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraListDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompraServiceUtil {
    private final UsuarioServiceUtil usuarioServiceUtil;
    private final ObjectMapper objectMapper;
    private final ItemRepository itemRepository;
    private final CompraRepository compraRepository;
    private final CotacaoXItemRepository cotacaoXItemRepository;

    public CompraEntity findByIDCompra(Integer idCompra) throws EntidadeNaoEncontradaException {
        return compraRepository.findById(idCompra)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Esta não compra não existe"));
    }

    public void verificarCompraDoUserLogado(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();
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

    public CompraWithValorItensDTO converterCompraEntityToCompraWithValor (CompraEntity compra) {

        CotacaoDTO cotacaoDTOList = compra.getCotacoes()
                .stream()
                .map(cotacaoEntity -> {
                    Set<ItemEntity> itemEntityList = cotacaoEntity.getCompra().getItens();
                    CompraWithValorItensDTO compraDTO = new CompraWithValorItensDTO();
                    CotacaoDTO cotacaoDTO = new CotacaoDTO();
                    List<ItemValorizadoDTO> itensComValorDTO = itemEntityList.stream()
                            .map(item -> {

                                ItemValorizadoDTO itemComValor = new ItemValorizadoDTO();

                                CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
                                cotacaoXItemPK.setIdCotacao(cotacaoEntity.getIdCotacao());
                                cotacaoXItemPK.setIdItem(item.getIdItem());

                                CotacaoXItemEntity cotacaoXItem = cotacaoXItemRepository.findById(cotacaoXItemPK).get();
                                itemComValor.setIdItem(item.getIdItem());
                                itemComValor.setNome(item.getNome());
                                itemComValor.setValorUnitario(cotacaoXItem.getValorDoItem());
                                itemComValor.setQuantidade(item.getQuantidade());
                                itemComValor.setValorTotal(cotacaoXItem.getValorTotal());
                                return itemComValor;

                            }).toList();

                    compraDTO.setItens(itensComValorDTO);
                    cotacaoDTO.setCompraDTO(compraDTO);
                    return cotacaoDTO;

                }).findFirst().get();

        List<ItemValorizadoDTO> itemValorizadoDTOS = cotacaoDTOList
                .getCompraDTO().getItens();

        Double valorCompra = 0.0;

        for(ItemValorizadoDTO itemValorizadoDTO : itemValorizadoDTOS){
            valorCompra += itemValorizadoDTO.getValorUnitario() * itemValorizadoDTO.getQuantidade();
        }

        CompraWithValorItensDTO compraDTO = objectMapper.convertValue(compra, CompraWithValorItensDTO.class);
        compraDTO.setValor(valorCompra);
        compraDTO.setItens(itemValorizadoDTOS);
        compraDTO.setNomeDoUsuario(compra.getUsuario().getNome());

        return compraDTO;
    }

    public CompraListDTO converterEntityParaListDTO(CompraEntity compra) {
        List<ItemDTO> itemDTOList = compra.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                .toList();
        CompraListDTO listDTO = objectMapper.convertValue(compra, CompraListDTO.class);
        listDTO.setStatus(compra.getStatus().name());
        listDTO.setItens(itemDTOList);
        return listDTO;
    }
}
