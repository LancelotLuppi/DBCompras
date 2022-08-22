package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
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

//    @Test
//    public void deveTestarSalvarItensDaCompra() {
//        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
//
//        when(itemRepository.save(any(ItemEntity.class))).thenReturn();
//    }

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

        compra.setItens(Set.of());
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

    private static UsuarioEntity getUsuarioEntity() {
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
