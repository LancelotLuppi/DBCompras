package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.SituacaoCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    private final ItemService itemService;

    private CompraDTO converterCompraEntityToCompraDTO(CompraEntity compra) {
        return objectMapper.convertValue(compra, CompraDTO.class);
    }

    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
        compra.setDataCompra(LocalDateTime.now());
        compra.setStatus(SituacaoCompra.ABERTO);
        compra.setUsuario(usuario);
        compra.getItens().clear();
        CompraEntity compraSalva = compraRepository.save(compra);

        Set<ItemEntity> itens = compraCreateDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .peek(itemEntity -> itemEntity.setCompra(compraSalva))
                .map(itemRepository::save)
                .collect(Collectors.toSet());

        return converterCompraEntityToCompraDTO(compra);

    }

    public CompraEntity findById (Integer id){
        return objectMapper.convertValue(compraRepository.findById(id), CompraEntity.class);
    }

    public CompraDTO update (Integer idCompra , CompraUpdateDTO compraDTO, SituacaoCompra status) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        CompraEntity compra = findById(idCompra);

        if(compraDTO.getName() != null){
            compra.setName(compraDTO.getName());
        }

        if(compraDTO.getValor() != null){
            compra.setValor(compraDTO.getValor());
        }

        if(status != null){
            compra.setStatus(status);
        }

        if(!compraDTO.getItens().isEmpty()){

            Set<ItemEntity> listaDeItens = compraDTO.getItens().stream()
                    .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                    .peek(itemEntity -> itemEntity.setCompra(compra))
                    .map(itemRepository::save)
                    .collect(Collectors.toSet());

            compra.setItens(listaDeItens);

        }

        compraRepository.save(compra);
        return converterCompraEntityToCompraDTO(compra);

    }

    public List<CompraDTO> list() {

        return compraRepository.findAll()
                .stream()
                .map(this::converterCompraEntityToCompraDTO)
                .toList();

    }

    public void delete(Integer id) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();
        compraRepository.deleteById(id);

    }

    public void verificarCompraDoUserLogado(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();
        List<Integer> idCompras = usuario.getCompras().stream()
                .map(CompraEntity::getIdCompra).toList();
        if (!idCompras.contains(idCompra)) {
            throw new RegraDeNegocioException("Esta compra não é sua!");
        }
    }

    private CompraEntity converterCompraCreateDTOToCompraEntity(CompraCreateDTO compraCreateDTO) {

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);

        Set<ItemEntity> itens = compraCreateDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .collect(Collectors.toSet());

        compra.getItens().clear();
        compra.setItens(itens);

        compra.setDataCompra(LocalDateTime.now());

        itens.stream().map(itemRepository::save);

        return compra;
    }
    public void removerItensDaCompra (Integer idCompra, Integer idItem){

        CompraEntity compra = findById(idCompra);

        Set<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        compra.setItens(itemEntities);
        compraRepository.save(compra);

    }

}
