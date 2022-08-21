package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private CompraServiceUtil compraServiceUtil;

    @Mock
    private ItemRepository itemRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(itemService, "objectMapper", objectMapper);
    }

    @Test
    public void deveAtualizarItemComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {
        ItemEntity item = getItemEntity();
        CompraEntity compra = getCompraEntity();
        item.setCompra(compra);
        ItemCreateDTO itemCreateDTO = getItemCreateDTO();
        Integer idItem = 10;

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(itemRepository.save(any(ItemEntity.class))).thenReturn(item);

        ItemDTO itemDTO = itemService.updateItem(idItem,itemCreateDTO);

        assertNotNull(itemDTO);
        assertEquals(itemDTO.getNome(), itemCreateDTO.getNome());
        assertEquals(itemDTO.getIdItem(), item.getIdItem());
        assertEquals(itemDTO.getQuantidade(), itemCreateDTO.getQuantidade());

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveNaoAtualizarItemComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {
        ItemEntity item = getItemEntity();
        CompraEntity compra = getCompraEntity();
        item.setCompra(compra);
        compra.setStatus(StatusCompra.COTADO);
        ItemCreateDTO itemCreateDTO = getItemCreateDTO();
        Integer idItem = 10;

        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());

        ItemDTO itemDTO = itemService.updateItem(idItem,itemCreateDTO);

    }

    private static ItemEntity getItemEntity () {
        ItemEntity item = new ItemEntity();
        item.setIdItem(10);
        item.setCompra(null);
        item.setNome("item");
        item.setCotacoes(null);
        item.setPreco(10.0);
        item.setQuantidade(10);
        return item;
    }

    private static ItemDTO getItemDTO () {
        ItemDTO item = new ItemDTO();
        item.setIdItem(10);
        item.setNome("item");
        item.setQuantidade(10);
        return item;
    }

    private static ItemCreateDTO getItemCreateDTO () {
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setQuantidade(10);
        itemCreateDTO.setNome("teste");
        return itemCreateDTO;
    }

    private static CompraEntity getCompraEntity () {
        CompraEntity compra = new CompraEntity();
        compra.setIdCompra(10);
        compra.setDataCompra(LocalDateTime.of(1991, 9, 8,10,20));
        compra.setStatus(StatusCompra.ABERTO);
        compra.setName("compra");
        compra.setDescricao("compra");
        compra.setValorTotal(10.0);
        compra.setItens(null);
        compra.setCotacoes(null);
        return compra;
    }


}
