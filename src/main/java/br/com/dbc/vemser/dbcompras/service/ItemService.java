package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;

    private final ObjectMapper objectMapper;

    public ItemDTO create (ItemCreateDTO itemCreateDTO) throws UsuarioException {

        ItemEntity item = objectMapper.convertValue(itemCreateDTO, ItemEntity.class);

        return objectMapper.convertValue(item, ItemDTO.class);


    }

    public void saveItensRepository (List<ItemEntity> itens) {

        itemRepository.saveAll(itens);

    }


}
