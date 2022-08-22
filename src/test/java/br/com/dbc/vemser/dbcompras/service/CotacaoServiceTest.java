package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.ComprasComCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoValorItensDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.CotacaoServiceUtil;
import br.com.dbc.vemser.dbcompras.util.ItemServiceUtil;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CotacaoServiceTest {

    @InjectMocks
    private CotacaoService cotacaoService;

    @Mock
    private CotacaoRepository cotacaoRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private CotacaoXItemRepository cotacaoXItemRepository;

    @Mock
    private CotacaoServiceUtil cotacaoServiceUtil;
    @Mock
    private EmailService emailService;
    private ItemServiceUtil itemServiceUtil = new ItemServiceUtil();
    private ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private CompraServiceUtil compraServiceUtil;

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(cotacaoService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(cotacaoService, "itemServiceUtil", itemServiceUtil);
    }

    @Test
    public void deveTestarCriarCotacaoComSucesso () throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {

        CotacaoEntity cotacao = getCotacaoEntity();
        CompraEntity compra = getCompraEntity();
        CotacaoDTO cotacaoDTO = getCotacaoDTO();
        CotacaoCreateDTO cotacaoCreateDTO = getCotacaoCreateDTO();
        CotacaoValorItensDTO cotacaoValorItensDTO = getCotacaoValorItensDTO();
        cotacaoDTO.setListaDeValores(List.of(cotacaoValorItensDTO));
        ItemEntity item = getItemEntity();
        CotacaoXItemEntity cotacaoXItem = getCotacaoXItem();
        List<CotacaoValorItensDTO> cotacaoValorItensDTOList = new ArrayList<>();
        compra.setItens(Set.of(item));
        cotacaoValorItensDTOList.add(cotacaoValorItensDTO);
        cotacaoCreateDTO.setListaDeValores(cotacaoValorItensDTOList);
        Integer idCotacao = 10;


        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        when(cotacaoRepository.save(any(CotacaoEntity.class))).thenReturn(cotacao);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(cotacaoXItemRepository.save(any(CotacaoXItemEntity.class))).thenReturn(cotacaoXItem);


        cotacaoService.create(idCotacao, cotacaoCreateDTO);

        verify(cotacaoRepository , times(2)).save(any(CotacaoEntity.class));
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCriarCotacaoComItensNaoPertencentes() throws RegraDeNegocioException, EntidadeNaoEncontradaException, UsuarioException {
        CompraEntity compra = getCompraEntity();
        CotacaoDTO cotacaoDTO = getCotacaoDTO();
        CotacaoCreateDTO cotacaoCreateDTO = getCotacaoCreateDTO();
        CotacaoValorItensDTO cotacaoValorItensDTO = getCotacaoValorItensDTO();
        cotacaoValorItensDTO.setIdItem(42);
        cotacaoDTO.setListaDeValores(List.of(cotacaoValorItensDTO));
        ItemEntity item = getItemEntity();

        List<CotacaoValorItensDTO> cotacaoValorItensDTOList = new ArrayList<>();
        compra.setItens(Set.of(item));
        cotacaoValorItensDTOList.add(cotacaoValorItensDTO);
        cotacaoCreateDTO.setListaDeValores(cotacaoValorItensDTOList);
        Integer idCompra = 10;


        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));


        cotacaoService.create(idCompra, cotacaoCreateDTO);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarCriarCotacaoSemTodosOsItens() throws RegraDeNegocioException, EntidadeNaoEncontradaException, UsuarioException {
        CompraEntity compra = getCompraEntity();
        CotacaoDTO cotacaoDTO = getCotacaoDTO();
        CotacaoCreateDTO cotacaoCreateDTO = getCotacaoCreateDTO();
        CotacaoValorItensDTO cotacaoValorItensDTO = getCotacaoValorItensDTO();
        cotacaoDTO.setListaDeValores(List.of(cotacaoValorItensDTO));
        ItemEntity item = getItemEntity();
        ItemEntity itemDois = getItemEntity();
        itemDois.setIdItem(12);

        Set<ItemEntity> itens = new HashSet<>();
        itens.add(item);
        itens.add(itemDois);

        List<CotacaoValorItensDTO> cotacaoValorItensDTOList = new ArrayList<>();
        compra.setItens(itens);
        cotacaoValorItensDTOList.add(cotacaoValorItensDTO);
        cotacaoCreateDTO.setListaDeValores(cotacaoValorItensDTOList);
        Integer idCompra = 10;


        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));


        cotacaoService.create(idCompra, cotacaoCreateDTO);
    }

    @Test
    public void deveTestarAprovarCotacaoComSucesso () throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CotacaoEntity cotacao = getCotacaoEntity();
        CompraEntity compra = getCompraEntity();
        cotacao.setCompra(compra);
        Integer idCotacao = 10;
        CotacaoDTO cotacaoDTO = getCotacaoDTO();
        EnumAprovacao aprovacao = EnumAprovacao.APROVAR;
        cotacaoDTO.setStatus(StatusCotacao.APROVADO);

        when(cotacaoServiceUtil.findById(anyInt())).thenReturn(cotacao);
        when(compraServiceUtil.findByIDCompra(anyInt())).thenReturn(compra);
        doNothing().when(cotacaoServiceUtil).verificarStatusDaCompraAndCotacao(eq(compra), eq(cotacao));
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(cotacaoRepository.save(any(CotacaoEntity.class))).thenReturn(cotacao);
        when(cotacaoServiceUtil.converterCotacaoToCotacaoDTO(any(CotacaoEntity.class))).thenReturn(cotacaoDTO);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), any(StatusCompra.class));

        CotacaoDTO cotacaoDTO1 = cotacaoService.aprovarOuReprovarCotacao(idCotacao, aprovacao);

        assertNotNull(cotacaoDTO1);
        assertEquals(cotacaoDTO1.getStatus(), cotacaoDTO.getStatus());
    }

    @Test
    public void deveTestarListarComprasComCotacoesComSucesso() {
        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        CotacaoEntity cotacao = getCotacaoEntity();
        CotacaoXItemEntity cotacaoXItem = getCotacaoXItem();
        compra.setItens(Set.of(item));
        compra.setCotacoes(Set.of(cotacao));

        when(compraRepository.findAll()).thenReturn(List.of(compra));
        when(cotacaoXItemRepository.findById(any(CotacaoXItemPK.class))).thenReturn(Optional.of(cotacaoXItem));

        List<ComprasComCotacaoDTO> comprasDTO = cotacaoService.listarCompraComCotacao();

        assertNotNull(comprasDTO);
        assertEquals(100, comprasDTO.get(0).getValorTotal(), 0);
    }

    private static CotacaoValorItensDTO getCotacaoValorItensDTO () {
        CotacaoValorItensDTO cotacaoValorItensDTO = new CotacaoValorItensDTO();
        cotacaoValorItensDTO.setValorDoItem(10.0);
        cotacaoValorItensDTO.setIdItem(10);
        return cotacaoValorItensDTO;
    }

    private static CotacaoCreateDTO getCotacaoCreateDTO () {
        CotacaoCreateDTO cotacaoCreateDTO = new CotacaoCreateDTO();
        cotacaoCreateDTO.setAnexo("PBKDF2WithHmacSHA256");
        cotacaoCreateDTO.setNome("teste");
        return cotacaoCreateDTO;
    }

    private static CotacaoDTO getCotacaoDTO () {
        CotacaoDTO cotacaoDTO = new CotacaoDTO();
        cotacaoDTO.setIdCotacao(10);
        cotacaoDTO.setNome("teste");
        cotacaoDTO.setValor(100.0);
        cotacaoDTO.setAnexo("teste");
        cotacaoDTO.setStatus(StatusCotacao.EM_ABERTO);
        return cotacaoDTO;
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
        cotacao.setNome("Teste");
        cotacao.setLocalDate(LocalDateTime.now());
        cotacao.setStatus(StatusCotacao.EM_ABERTO);
        cotacao.setValor(10.0);
        String str = "byte array size example";
        byte array[] = str.getBytes();
        cotacao.setAnexo(array);
        return cotacao;
    }

    private static ItemEntity getItemEntity () {
        ItemEntity item = new ItemEntity();
        item.setIdItem(10);
        item.setCompra(null);
        item.setNome("item");
        item.setCotacoes(null);
        item.setQuantidade(10);
        return item;
    }

    private static CompraEntity getCompraEntity () {
        CompraEntity compra = new CompraEntity();
        UsuarioEntity user = new UsuarioEntity();
        user.setNome("Nome user");
        compra.setUsuario(user);
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
