package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraListDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraUpdateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.SituacaoCompra;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.ItemServiceUtil;
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompraService {

    private final ObjectMapper objectMapper;
    private final CompraRepository compraRepository;
    private final CompraServiceUtil compraServiceUtil;
    private final ItemRepository itemRepository;
    private final UsuarioServiceUtil usuarioServiceUtil;
    private final ItemServiceUtil itemServiceUtil;


    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
        compra.setDataCompra(LocalDateTime.now());
        compra.setStatus(SituacaoCompra.ABERTO.getSituacao());
        compra.setUsuario(usuario);
        CompraEntity compraSalva = compraRepository.save(compra);

        Set<ItemEntity> itens = compraServiceUtil.salvarItensDaCompra(compraCreateDTO, compraSalva);
        compraSalva.setItens(itens);

        return compraServiceUtil.converterCompraEntityToCompraDTO(compraSalva);
    }

    public List<CompraListDTO> listColaborador(Integer idCompra) throws UsuarioException {

        if(idCompra != null){
            return compraRepository.findById(idCompra)
                   .stream()
                   .map(compraServiceUtil::converterEntityParaListDTO)
                   .toList();
        }else{
            return compraRepository.findAll().stream()
                    .map(compraServiceUtil::converterEntityParaListDTO)
                    .toList();
        }
    }

    public CompraDTO update(Integer idCompra, CompraUpdateDTO compraDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByID(idCompra);

        List<Integer> idItemRecebidoList = compraDTO.getItens().stream()
                .map(ItemUpdateDTO::getIdItem)
                .toList();

        itemServiceUtil.verificarItensDaCompra(compra, idItemRecebidoList);

        compra.setName(compra.getName());
        compra.setDescricao(compraDTO.getDescricao());
        compraDTO.getItens()
                .forEach(item -> {
                    ItemEntity itemEntity = itemRepository.findById(item.getIdItem()).get();
                    itemEntity.setNome(item.getNome());
                    itemEntity.setQuantidade(item.getQuantidade());
                    itemRepository.save(itemEntity);
                });

        CompraEntity compraAtualizada = compraRepository.save(compra);
        return compraServiceUtil.converterCompraEntityToCompraDTO(compraAtualizada);
    }

    public void delete(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        compraRepository.deleteCompra(idCompra);
    }


    public void removerItensDaCompra(Integer idCompra, Integer idItem) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByID(idCompra);

        Set<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        itemRepository.delete(itemRepository.findById(idItem).get());
        compra.setItens(itemEntities);
        compraRepository.save(compra);
        compraServiceUtil.converterCompraEntityToCompraDTO(compra);
    }

    public List<CompraRelatorioDTO> relatorioCompras (Integer idCompra){
        return compraRepository.findByCompraId(idCompra);
    }

}
