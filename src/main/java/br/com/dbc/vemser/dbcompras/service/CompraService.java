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
import br.com.dbc.vemser.dbcompras.util.CompraUtil;
import br.com.dbc.vemser.dbcompras.util.UsuarioUtil;
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
    private final CompraUtil compraUtil;
    private final ItemRepository itemRepository;
    private final UsuarioUtil usuarioUtil;


    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioUtil.retornarUsuarioEntityLogado();

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
        compra.setDataCompra(LocalDateTime.now());
        compra.setStatus(SituacaoCompra.ABERTO);
        compra.setUsuario(usuario);
        CompraEntity compraSalva = compraRepository.save(compra);

        Set<ItemEntity> itens = compraUtil.salvarItensDaCompra(compraCreateDTO, compraSalva);
        compraSalva.setItens(itens);

        return compraUtil.converterCompraEntityToCompraDTO(compraSalva);
    }

    public CompraDTO update(Integer idCompra, CompraCreateDTO compraDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        compraUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraUtil.findByID(idCompra);

        if (compraDTO.getName() != null) {
            compra.setName(compraDTO.getName());
        }
        if (!compraDTO.getItens().isEmpty()) {
            Set<ItemEntity> itensAntigos = compra.getItens();
            itensAntigos.forEach(itemRepository::delete);

            compra.getItens().clear();
            Set<ItemEntity> novosItens = new HashSet<>(compraUtil.salvarItensDaCompra(compraDTO, compra));
            compra.setItens(novosItens);
        }

        CompraEntity compraAtualizada = compraRepository.save(compra);
        return compraUtil.converterCompraEntityToCompraDTO(compraAtualizada);
    }

    public void delete(Integer id) throws UsuarioException, RegraDeNegocioException {
        compraUtil.verificarCompraDoUserLogado(id);
        compraRepository.deleteById(id);
    }

    public void removerItensDaCompra(Integer idCompra, Integer idItem) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        compraUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraUtil.findByID(idCompra);

        Set<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        itemRepository.delete(itemRepository.findById(idItem).get());
        compra.setItens(itemEntities);
        compraRepository.save(compra);
    }
}
