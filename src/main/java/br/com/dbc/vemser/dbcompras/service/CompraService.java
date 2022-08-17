package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
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
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
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


    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException {

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

    public CompraDTO update(Integer idCompra, CompraCreateDTO compraDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByID(idCompra);

        if (compraDTO.getName() != null) {
            compra.setName(compraDTO.getName());
        }
        if (!compraDTO.getItens().isEmpty()) {
            Set<ItemEntity> itensAntigos = compra.getItens();
            itensAntigos.forEach(itemRepository::delete);

            compra.getItens().clear();
            Set<ItemEntity> novosItens = new HashSet<>(compraServiceUtil.salvarItensDaCompra(compraDTO, compra));
            compra.setItens(novosItens);
        }

        CompraEntity compraAtualizada = compraRepository.save(compra);
        return compraServiceUtil.converterCompraEntityToCompraDTO(compraAtualizada);
    }

    public void delete(Integer id) throws UsuarioException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(id);
        CompraEntity compra = compraRepository.findById(id).get();
        Set<ItemEntity> itensAntigos = compra.getItens();
        itensAntigos.forEach(itemRepository::delete);
        compra.getItens().clear();
        compraRepository.delete(compra);
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
}
