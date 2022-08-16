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
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompraService {

    private final ObjectMapper objectMapper;

    private final CompraRepository compraRepository;

    private final UsuarioService usuarioService;
    private final ItemRepository itemRepository;


    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
        compra.setDataCompra(LocalDateTime.now());
        compra.setStatus(SituacaoCompra.ABERTO);
        compra.setUsuario(usuario);
        CompraEntity compraSalva = compraRepository.save(compra);
        salvarItensDaCompra(compraCreateDTO, compraSalva);

        return converterCompraEntityToCompraDTO(compra);

    }

    public CompraDTO update(Integer idCompra, CompraCreateDTO compraDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = findByID(idCompra);

        if (compraDTO.getName() != null) {
            compra.setName(compraDTO.getName());
        }
        if (!compraDTO.getItens().isEmpty()) {
            Set<ItemEntity> itensAntigos = compra.getItens();
            itensAntigos.forEach(itemRepository::delete);

            compra.getItens().clear();
            Set<ItemEntity> novosItens = new HashSet<>(salvarItensDaCompra(compraDTO, compra));
            compra.setItens(novosItens);
        }

        compraRepository.save(compra);
        return converterCompraEntityToCompraDTO(compra);
    }

    private CompraEntity findByID(Integer idCompra) throws UsuarioException, EntidadeNaoEncontradaException {
        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        Set<CompraEntity> compras = usuario.getCompras();

        return compras.stream()
                .filter(compraEntity -> compraEntity.getIdCompra().equals(idCompra))
                .findFirst()
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Esta não compra não foi criada ainda"));
    }

    public List<CompraDTO> list() throws UsuarioException {
        return usuarioService.retornarUsuarioEntityLogado()
                .getCompras()
                .stream()
                .map(this::converterCompraEntityToCompraDTO)
                .toList();
    }

    public void delete(Integer id) throws UsuarioException, RegraDeNegocioException {
        verificarCompraDoUserLogado(id);
        compraRepository.deleteById(id);
    }

    public void removerItensDaCompra(Integer idCompra, Integer idItem) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = findByID(idCompra);

        Set<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        compra.setItens(itemEntities);
        compraRepository.save(compra);
    }

    public void verificarCompraDoUserLogado(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();
        List<Integer> idCompras = usuario.getCompras().stream()
                .map(CompraEntity::getIdCompra).toList();
        if (!idCompras.contains(idCompra)) {
            throw new RegraDeNegocioException("Esta compra não é sua!");
        }
    }

    private Set<ItemEntity> salvarItensDaCompra(CompraCreateDTO compraCreateDTO, CompraEntity compraSalva) {
        return recuperarListaItensDoDto(compraCreateDTO).stream()
                .peek(itemEntity -> itemEntity.setCompra(compraSalva))
                .map(itemRepository::save)
                .collect(Collectors.toSet());
    }

    private Set<ItemEntity> recuperarListaItensDoDto(CompraCreateDTO compraCreateDTO) {
        return compraCreateDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .collect(Collectors.toSet());
    }

    private CompraDTO converterCompraEntityToCompraDTO(CompraEntity compra) {
        return objectMapper.convertValue(compra, CompraDTO.class);
    }
}
