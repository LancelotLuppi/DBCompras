package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    private final ObjectMapper objectMapper;

    public ItemDTO create (ItemCreateDTO itemCreateDTO) throws UsuarioException {

        ItemEntity item = objectMapper.convertValue(itemCreateDTO, ItemEntity.class);
        itemRepository.save(item);
        return objectMapper.convertValue(item, ItemDTO.class);

    }

    public ItemEntity findById (Integer idItem) throws EntidadeNaoEncontradaException {
        return itemRepository.findById(idItem)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Este item não existe"));
    }

    public ItemDTO update (ItemCreateDTO itemUpdateDTO, Integer idItem) throws EntidadeNaoEncontradaException {

        ItemEntity item = findById(idItem);

        if(itemUpdateDTO.getNome() != null){
            item.setNome(itemUpdateDTO.getNome());
        }

        if(itemUpdateDTO.getQuantidade() != null){
            item.setQuantidade(itemUpdateDTO.getQuantidade());
        }

        itemRepository.save(item);
        return objectMapper.convertValue(item, ItemDTO.class);
    }

}
