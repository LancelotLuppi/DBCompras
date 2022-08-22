package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.compra.*;
import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.checkerframework.checker.nullness.Opt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompraServiceUtilTest {
    @InjectMocks
    private CompraServiceUtil compraServiceUtil;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CompraRepository compraRepository;
    @Mock
    private CotacaoXItemRepository cotacaoXItemRepository;
    @Mock
    private UsuarioServiceUtil usuarioServiceUtil;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(compraServiceUtil, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarFindByIdCompraComSucesso() throws EntidadeNaoEncontradaException {
        CompraEntity compra = getCompraEntity();
        int idCompra = 10;

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));

        CompraEntity compraTeste = compraServiceUtil.findByIDCompra(10);

        assertNotNull(compraTeste);
        assertEquals(10, compraTeste.getIdCompra());
        assertEquals(LocalDateTime.of(1991, 9, 8, 10, 20), compraTeste.getDataCompra());
        assertEquals(StatusCompra.ABERTO, compraTeste.getStatus());
        assertEquals("compra", compraTeste.getName());
        assertEquals("descricao", compraTeste.getDescricao());
        assertEquals(10, compraTeste.getValorTotal(), 0);
        assertEquals(1, compraTeste.getItens().size());
    }

    @Test(expected = EntidadeNaoEncontradaException.class)
    public void deveTestarFindByIdCompraSemEntidade() throws EntidadeNaoEncontradaException {
        int idCompra = 10;
        when(compraRepository.findById(anyInt())).thenReturn(Optional.empty());
        compraServiceUtil.findByIDCompra(idCompra);
    }

    @Test
    public void deveTestarVerificarCompraDoUserLogado() throws UsuarioException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        UsuarioEntity usuario = getUsuarioEntity();
        usuario.setCompras(Set.of(compra));

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);

        compraServiceUtil.verificarCompraDoUserLogado(10);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarVerificarCompraDoUserLogadoComCompraNaoPertencente() throws UsuarioException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        UsuarioEntity usuario = getUsuarioEntity();
        usuario.setCompras(Set.of(compra));

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);

        compraServiceUtil.verificarCompraDoUserLogado(12);
    }

    @Test
    public void deveTestarSalvarItensDaCompra() {
        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
        CompraEntity compraSalva = getCompraEntity();
        ItemEntity itemEntity = getItemEntity();

        when(itemRepository.save(any(ItemEntity.class))).thenReturn(itemEntity);

        Set<ItemEntity> itens = compraServiceUtil.salvarItensDaCompra(compraCreateDTO, compraSalva);

        assertNotNull(itens);
        assertEquals(1, itens.size());
        assertEquals(12, itens.stream().findFirst().get().getIdItem());
    }

    @Test
    public void deveTestarConverterCompraEntityToCompraCreateDTO() {
        CompraEntity compra = getCompraEntity();
        CompraDTO compraDTO = compraServiceUtil.converterCompraEntityToCompraDTO(compra);

        assertNotNull(compraDTO);
        assertEquals(1, compraDTO.getItens().size());
        assertEquals(10, compraDTO.getIdCompra());
        assertEquals("compra", compraDTO.getName());
        assertEquals("descricao", compraDTO.getDescricao());
    }


    @Test
    public void deveTestarConverterCompraEntityToCompraWithValorComSucesso() throws RegraDeNegocioException {
        UsuarioEntity usuario = getUsuarioEntity();
        CompraEntity compra = getCompraEntity();
        compra.setUsuario(usuario);
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        CotacaoEntity cotacao = getCotacaoEntity();
        cotacao.setCompra(compra);
        CotacaoXItemEntity cotacaoXItemEntity = getCotacaoXItemEntity();
        cotacaoXItemEntity.setCotacaoXItemPK(getCotacaoXItemPK());
        cotacaoXItemEntity.setItem(getItemEntity());
        cotacaoXItemEntity.setCotacao(cotacao);
        cotacao.setItens(Set.of(cotacaoXItemEntity));
        compra.setCotacoes(Set.of(cotacao));

        when(cotacaoXItemRepository.findById(any())).thenReturn(Optional.of(cotacaoXItemEntity));

        CompraWithValorItensDTO compraRetorno = compraServiceUtil.converterCompraEntityToCompraWithValor(compra);

        assertNotNull(compraRetorno);
        assertEquals(1, compraRetorno.getItens().size());
    }

    @Test
    public void deveTestarConverterEntityParaListDTOComSucesso() {
        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));

        CompraListDTO listDTO = compraServiceUtil.converterEntityParaListDTO(compra);

        assertNotNull(listDTO);
        assertEquals(10, listDTO.getIdCompra());
        assertEquals("compra", listDTO.getName());
        assertEquals("descricao", listDTO.getDescricao());
        assertEquals(1, listDTO.getItens().size());
    }

    private static CompraEntity getCompraEntity() {
        CompraEntity compra = new CompraEntity();

        compra.setIdCompra(10);
        compra.setDataCompra(LocalDateTime.of(1991, 9, 8, 10, 20));
        compra.setUsuario(getUsuarioEntity());
        compra.setStatus(StatusCompra.ABERTO);
        compra.setName("compra");
        compra.setDescricao("descricao");
        compra.setValorTotal(10.0);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setIdItem(12);
        itemEntity.setNome("batata");
        itemEntity.setQuantidade(3);

        compra.setItens(Set.of(itemEntity));
        compra.setCotacoes(null);
        return compra;
    }

    public ItemEntity getItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setIdItem(12);
        itemEntity.setNome("batata");
        itemEntity.setQuantidade(3);
        return itemEntity;
    }

    public CotacaoEntity getCotacaoEntity() {
        CotacaoEntity cotacao = new CotacaoEntity();
        cotacao.setIdCotacao(10);
        cotacao.setNome("Cotacao");
        cotacao.setValor(30.0);
        return cotacao;
    }

    public CotacaoXItemEntity getCotacaoXItemEntity() {
        CotacaoXItemEntity cotacaoXItem = new CotacaoXItemEntity();
        cotacaoXItem.setValorDoItem(10.0);
        cotacaoXItem.setValorTotal(30.0);

        return cotacaoXItem;
    }

    public CotacaoXItemPK getCotacaoXItemPK() {
        CotacaoEntity cotacao = getCotacaoEntity();
        ItemEntity item =getItemEntity();
        CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
        cotacaoXItemPK.setIdCotacao(cotacao.getIdCotacao());
        cotacaoXItemPK.setIdItem(item.getIdItem());
        return cotacaoXItemPK;
    }

    private static UsuarioEntity getUsuarioEntity() {
        UsuarioEntity usuario = new UsuarioEntity();
        CargoEntity cargo = new CargoEntity();
        String str = "imagem";
        byte array[] = str.getBytes();
        CompraEntity compra = new CompraEntity();
        CotacaoEntity cotacao = new CotacaoEntity();
        usuario.setNome("Rodrigo");
        usuario.setCargos(Set.of(cargo));
        usuario.setPassword("AtackOnT1t@n");
        usuario.setEnable(true);
        usuario.setPhoto(array);
        usuario.setCompras(Set.of(compra));
        usuario.setIdUser(10);
        usuario.setEmail("teste@bdccompany.com.br");
        return usuario;
    }

    public CompraCreateDTO getCompraCreateDTO() {
        CompraCreateDTO compraCreateDTO = new CompraCreateDTO();
        compraCreateDTO.setName("Compra create");
        compraCreateDTO.setDescricao("Descricao criacao da compra");
        ItemCreateDTO itemCreateDTO = new ItemCreateDTO();
        itemCreateDTO.setNome("Meu item teste");
        itemCreateDTO.setQuantidade(3);
        compraCreateDTO.setItens(List.of(itemCreateDTO));
        return compraCreateDTO;
    }
}
