package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
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

    private final UsuarioService usuarioService;

    private final ItemService itemService;

    private CompraDTO converterCompraEntityToCompraDTO(CompraEntity compra) {
        return objectMapper.convertValue(compra, CompraDTO.class);
    }

    private CompraEntity converterCompraCreateDTOToCompraEntity(CompraCreateDTO compraCreateDTO) {
        return objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
    }

    public CompraDTO create (CompraCreateDTO compraCreateDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityById();

        CompraEntity compra = converterCompraCreateDTOToCompraEntity(compraCreateDTO);
        compra.setDataCompra(LocalDateTime.now());
        compra.setUsuario(usuario);
        compraRepository.save(compra);

        return converterCompraEntityToCompraDTO(compra);

    }

    public CompraEntity findById (Integer id){
        return objectMapper.convertValue(compraRepository.findById(id), CompraEntity.class);
    }

    public CompraDTO update (Integer idCompra , CompraUpdateDTO compraDTO, StatusCotacoes status) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityById();

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

            List<ItemEntity> listaDeItens = compraDTO.getItens()
                            .stream()
                            .map(itemDTO -> new ItemEntity(itemDTO.getNome(),itemDTO.getQuantidade()))
                            .toList();

            itemService.saveItensRepository(listaDeItens);
            List<ItemEntity> itensSalvos = compra.getItens();
            itensSalvos.addAll(listaDeItens);
            compra.setItens(itensSalvos);

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

    public void delete (Integer id) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityById();
        compraRepository.deleteById(id);

    }

    public void removerItensDaCompra (Integer idCompra, Integer idItem){

        CompraEntity compra = findById(idCompra);

        List<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        compra.setItens(itemEntities);
        compraRepository.save(compra);

    }

}
