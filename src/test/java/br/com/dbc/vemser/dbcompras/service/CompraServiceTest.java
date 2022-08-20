package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.ItemServiceUtil;
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompraServiceTest {

    @InjectMocks
    private CompraService compraService;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private UsuarioServiceUtil usuarioServiceUtil;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    CompraServiceUtil compraServiceUtil;

    @Mock
    private ItemServiceUtil itemServiceUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(compraService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarSeCriarCompraComSucesso () throws UsuarioException, RegraDeNegocioException {

        CompraEntity compra = getCompraEntity();
        UsuarioEntity usuario = getUsuarioEntity();
        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
        ItemEntity item = getItemEntity();
        Set<ItemEntity> itens = Set.of(item);
        CompraDTO compraDTO = getCompraDTO();
        List<ItemCreateDTO> itemCreateDTOS = List.of(getItemCreateDTO());
        compraCreateDTO.setItens(itemCreateDTOS);

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.salvarItensDaCompra(eq(compraCreateDTO), eq(compra))).thenReturn(itens);
        when(compraServiceUtil.converterCompraEntityToCompraDTO(any(CompraEntity.class))).thenReturn(compraDTO);

        CompraDTO compraDTO1 = compraService.create(compraCreateDTO);

        assertNotNull(compraDTO1);
        assertEquals(compraDTO1.getName(), compraDTO.getName());
        assertEquals(compraDTO1.getDescricao(), compraDTO.getDescricao());
        assertEquals(compraDTO1.getItens(), compraDTO.getItens());

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveNaoCriarListaDeComprasSemItens () throws UsuarioException, RegraDeNegocioException {

        CompraEntity compra = getCompraEntity();
        UsuarioEntity usuario = getUsuarioEntity();
        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
        ItemEntity item = getItemEntity();
        Set<ItemEntity> itens = Set.of(item);
        CompraDTO compraDTO = getCompraDTO();
        List<ItemCreateDTO> itemCreateDTOS = new ArrayList<>();
        compraCreateDTO.setItens(itemCreateDTOS);

        CompraDTO compraDTO1 = compraService.create(compraCreateDTO);

    }

    private static CompraCreateDTO getCompraCreateDTO () {
        CompraCreateDTO compraCreateDTO = new CompraCreateDTO();
        compraCreateDTO.setDescricao("teste");
        compraCreateDTO.setName("teste");
        return compraCreateDTO;
    }

    private static ItemCreateDTO getItemCreateDTO () {
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setNome("teste");
        itemCreateDTO.setQuantidade(10);
        return itemCreateDTO;
    }

    private static CompraDTO getCompraDTO () {
        CompraDTO compraDTO = new CompraDTO();
        compraDTO.setIdCompra(10);
        compraDTO.setDescricao("teste");
        compraDTO.setName("teste");
        return compraDTO;
    }

    private static ItemDTO getItemDTO () {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setIdItem(10);
        itemDTO.setNome("teste");
        itemDTO.setQuantidade(7);
        return itemDTO;
    }

    private static CotacaoXItemPK getCotacaoXItemPK () {
        CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
        cotacaoXItemPK.setIdCotacao(10);
        cotacaoXItemPK.setIdItem(10);
        return cotacaoXItemPK;
    }

    private static CotacaoXItemEntity getCotacaoXItem () {
        CotacaoXItemEntity cotacaoXItem = new CotacaoXItemEntity();
        cotacaoXItem.setValorDoItem(10.0);
        cotacaoXItem.setValorTotal(10.0);
        return cotacaoXItem;
    }

    private static CotacaoEntity getCotacaoEntity () {
        CotacaoEntity cotacao = new CotacaoEntity();
        cotacao.setIdCotacao(10);
        cotacao.setLocalDate(LocalDateTime.now());
        cotacao.setStatus(StatusCotacao.EM_ABERTO);
        cotacao.setValor(10.0);
        cotacao.setAnexo(null);
        cotacao.setUsuario(null);
        return cotacao;
    }

    private static ItemEntity getItemEntity () {
        ItemEntity item = new ItemEntity();
        item.setIdItem(10);
        item.setCompra(null);
        item.setNome("item");
        item.setCotacoes(null);
        item.setPreco(10.0);
        return item;
    }

    private static CompraEntity getCompraEntity () {
        CompraEntity compra = new CompraEntity();
        compra.setIdCompra(10);
        compra.setDataCompra(LocalDateTime.of(1991, 9, 8,10,20));
        compra.setUsuario(getUsuarioEntity());
        compra.setStatus(StatusCompra.ABERTO);
        compra.setName("compra");
        compra.setDescricao("compra");
        compra.setValorTotal(10.0);
        compra.setItens(null);
        compra.setCotacoes(null);
        return compra;
    }

    private static UsuarioEntity getUsuarioEntity (){
        UsuarioEntity usuario = new UsuarioEntity();
        CargoEntity cargo = new CargoEntity();
        String str = "byte array size example";
        byte array[] = str.getBytes();
        CompraEntity compra = new CompraEntity();
        CotacaoEntity cotacao = new CotacaoEntity();
        usuario.setNome("Rodrigo");
        usuario.setCargos(Set.of(cargo));
        usuario.setPassword("AtackOnT1t@n");
        usuario.setEnable(true);
        usuario.setPhoto(array);
        usuario.setCompras(Set.of(compra));
        usuario.setCotacoes(Set.of(cotacao));
        usuario.setIdUser(10);
        usuario.setEmail("teste@bdccompany.com.br");
        return usuario;

    }
}
