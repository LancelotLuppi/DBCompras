package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.*;
import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemUpdateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
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
import java.util.*;

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
    @Mock
    private EmailService emailService;
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
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

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

        compraService.create(compraCreateDTO);

    }

    @Test
    public void deveListarComprasDoColaboradorComSucesso () throws UsuarioException, RegraDeNegocioException {
        List<CompraEntity> compras = new ArrayList<>();
        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        compras.add(compra);
        CompraListDTO compraListDTO = getCompraListDTO();
        Integer id = 10;
        ItemDTO itemDTO = getItemDTO();
        compraListDTO.setItens(List.of(itemDTO));


        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        when(compraServiceUtil.converterEntityParaListDTO(any(CompraEntity.class))).thenReturn(compraListDTO);

        List<CompraListDTO> compraListDTOS = compraService.listColaborador(id);

        assertNotNull(compraListDTOS);
        assertFalse(compraListDTOS.isEmpty());
    }

    @Test
    public void deveListarTodasAsComprasDosColaboradoresComSucesso () throws UsuarioException, RegraDeNegocioException {
        List<CompraEntity> compras = new ArrayList<>();
        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        compras.add(compra);
        CompraListDTO compraListDTO = getCompraListDTO();
        Integer id = null;
        Integer numero = 10;
        ItemDTO itemDTO = getItemDTO();
        compraListDTO.setItens(List.of(itemDTO));

        when(compraRepository.findAllByUsuarioId(anyInt())).thenReturn(compras);
        when(usuarioServiceUtil.getIdLoggedUser()).thenReturn(numero);
        when(compraServiceUtil.converterEntityParaListDTO(any(CompraEntity.class))).thenReturn(compraListDTO);

        List<CompraListDTO> compraListDTOS = compraService.listColaborador(id);

        assertNotNull(compraListDTOS);
        assertFalse(compraListDTOS.isEmpty());
    }

    @Test
    public void deveAtualizarCompraComItensComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {


        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
        ItemCreateDTO itemCreateDTO = getItemCreateDTO();
        List<ItemCreateDTO> itemCreateDTOS = new ArrayList<>();
        itemCreateDTOS.add(itemCreateDTO);
        compraCreateDTO.setItens(itemCreateDTOS);
        List<Integer> idsItens = new ArrayList<>();
        CompraDTO compraDTO = getCompraDTO();
        Integer idCompra = 10;

        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(compraServiceUtil.findByID(anyInt())).thenReturn(compra);
        when(itemRepository.save(any(ItemEntity.class))).thenReturn(item);
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.converterCompraEntityToCompraDTO(any(CompraEntity.class))).thenReturn(compraDTO);

        CompraDTO compraDTO1 = compraService.updateTeste(idCompra, compraCreateDTO);

        assertNotNull(compraDTO1);
        assertEquals(compraDTO.getName(), compraDTO1.getName());
        assertEquals(compraDTO.getIdCompra(), compraDTO1.getIdCompra());
        assertEquals(compraDTO.getDescricao(), compraDTO1.getDescricao());
        assertEquals(compraDTO.getItens(), compraDTO1.getItens());
    }

    @Test
    public void deveAtualizarCompraSemDeletarItensComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {


        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        CompraUpdateDTO compraUpdateDTO = getCompraUpdateDTO();
        ItemUpdateDTO itemUpdateDTO = getItemUpdateDTO();
        List<ItemUpdateDTO> itemCreateDTOS = new ArrayList<>();
        itemCreateDTOS.add(itemUpdateDTO);
        compraUpdateDTO.setItens(itemCreateDTOS);
        List<Integer> idsItens = new ArrayList<>();
        CompraDTO compraDTO = getCompraDTO();
        Integer idCompra = 10;

        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(compraServiceUtil.findByID(anyInt())).thenReturn(compra);
        doNothing().when(itemServiceUtil).verificarItensDaCompra(eq(compra), anyList());
        when(itemRepository.save(any(ItemEntity.class))).thenReturn(item);
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.converterCompraEntityToCompraDTO(any(CompraEntity.class))).thenReturn(compraDTO);

        CompraDTO compraDTO1 = compraService.update(idCompra, compraUpdateDTO);

        assertNotNull(compraDTO1);
        assertEquals(compraDTO.getName(), compraDTO1.getName());
        assertEquals(compraDTO.getIdCompra(), compraDTO1.getIdCompra());
        assertEquals(compraDTO.getDescricao(), compraDTO1.getDescricao());
        assertEquals(compraDTO.getItens(), compraDTO1.getItens());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveNaoAtualizarCompraComItensComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {


        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        compra.setItens(Set.of(item));
        CompraCreateDTO compraCreateDTO = getCompraCreateDTO();
        ItemCreateDTO itemCreateDTO = getItemCreateDTO();
        List<ItemCreateDTO> itemCreateDTOS = new ArrayList<>();
        itemCreateDTOS.add(itemCreateDTO);
        compraCreateDTO.setItens(itemCreateDTOS);
        List<Integer> idsItens = new ArrayList<>();
        CompraDTO compraDTO = getCompraDTO();
        Integer idCompra = 10;
        StatusCompra statusCompra = StatusCompra.FECHADO;
        compra.setStatus(statusCompra);

        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(compraServiceUtil.findByID(anyInt())).thenReturn(compra);

        CompraDTO compraDTO1 = compraService.updateTeste(idCompra, compraCreateDTO);

    }


    @Test
    public void deveDeletarCompraComSucesso () throws UsuarioException, RegraDeNegocioException {

        Integer idCompra = 10;

        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        doNothing().when(compraRepository).deleteCompra(eq(idCompra));

        compraService.delete(idCompra);

        verify(compraRepository ,times(1)).deleteCompra(anyInt());

    }

    @Test
    public void deveRemoverItensDaCompraComSucesso () throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {

        CompraEntity compra = getCompraEntity();
        ItemEntity item = getItemEntity();
        Set<ItemEntity> itemEntities = new HashSet<>();
        itemEntities.add(item);
        compra.setItens(itemEntities);
        CompraDTO compraDTO = getCompraDTO();
        Integer idCompra = 10;
        Integer idItem = 10;

        doNothing().when(compraServiceUtil).verificarCompraDoUserLogado(anyInt());
        when(compraServiceUtil.findByID(anyInt())).thenReturn(compra);
        doNothing().when(itemServiceUtil).verificarItensDaCompra(eq(compra), anyList());
        doNothing().when(itemRepository).delete(any(ItemEntity.class));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.converterCompraEntityToCompraDTO(any(CompraEntity.class))).thenReturn(compraDTO);

        compraService.removerItensDaCompra(idCompra, idItem);

        verify(itemRepository, times(1)).delete(any(ItemEntity.class));
    }

    @Test
    public void deveRetornarCompraRelatorioDTOComSucesso () {
        List<CompraRelatorioDTO> compraRelatorioDTOList = new ArrayList<>();
        CompraRelatorioDTO compraRelatorioDTO = new CompraRelatorioDTO();
        CompraEntity compra = getCompraEntity();
        compraRelatorioDTO.setIdCompra(10);
        compraRelatorioDTOList.add(compraRelatorioDTO);
        Integer idCompra = 10;

        when(compraRepository.findByCompraId(anyInt())).thenReturn(compraRelatorioDTOList);
        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));


        List<CompraRelatorioRetornoDTO> dtoList = compraService.relatorioCompras(idCompra);

        assertNotNull(dtoList);
        assertFalse(dtoList.isEmpty());
    }

    @Test
    public void deveTestarAprovarCompraFinanceiro () throws EntidadeNaoEncontradaException, RegraDeNegocioException {

        CompraEntity compra = getCompraEntity();
        compra.setStatus(StatusCompra.APROVADO_GESTOR);
        EnumAprovacao aprovacao = EnumAprovacao.APROVAR;
        CompraWithValorItensDTO compraWithValorItensDTO = getCompraWithValorItensDTO();
        compraWithValorItensDTO.setStatus(StatusCompra.APROVADO_FINANCEIRO);
        Integer idCompra = 10;

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.converterCompraEntityToCompraWithValor(any(CompraEntity.class))).thenReturn(compraWithValorItensDTO);

        CompraWithValorItensDTO compra1 = compraService.aprovarReprovarCompra(idCompra , aprovacao);

        verify(compraRepository, times(1)).save(any(CompraEntity.class));
        assertEquals(compra1.getStatus(), compra.getStatus());


    }

    @Test
    public void deveTestarReprovarCompraFinanceiro  () throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        compra.setStatus(StatusCompra.APROVADO_GESTOR);
        StatusCompra statusCompra = StatusCompra.REPROVADO_FINANCEIRO;
        EnumAprovacao aprovacao = EnumAprovacao.REPROVAR;
        CompraWithValorItensDTO compraWithValorItensDTO = getCompraWithValorItensDTO();
        compraWithValorItensDTO.setStatus(statusCompra);
        Integer idCompra = 10;

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);
        when(compraServiceUtil.converterCompraEntityToCompraWithValor(any(CompraEntity.class))).thenReturn(compraWithValorItensDTO);

        CompraWithValorItensDTO compra1 = compraService.aprovarReprovarCompra(idCompra , aprovacao);

        verify(compraRepository, times(1)).save(any(CompraEntity.class));
        assertEquals(compra1.getStatus(), compra.getStatus());
    }

    @Test(expected = RegraDeNegocioException.class)
    public void devetestarAprovarCompraFinanceiroComCompraInvalida() throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        EnumAprovacao aprovacao = EnumAprovacao.APROVAR;
        Integer idCompra = 10;

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        compraService.aprovarReprovarCompra(idCompra, aprovacao);
    }

    @Test
    public void deveTestarFinalizarCotacoComSucesso () throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        CotacaoEntity cotacao = getCotacaoEntity();
        CotacaoEntity cotacao1 = getCotacaoEntity();
        Set<CotacaoEntity> cotacaoEntities = new HashSet<>();
        cotacaoEntities.add(cotacao1);
        cotacaoEntities.add(cotacao);
        compra.setCotacoes(cotacaoEntities);
        StatusCompra statusCompra = StatusCompra.COTADO;
        Integer idCompra = 10;
        CompraDTO compraDTO = getCompraDTO();

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));
        when(compraServiceUtil.converterCompraEntityToCompraDTO(any(CompraEntity.class))).thenReturn(compraDTO);
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compra);

        CompraDTO compraDTO1 = compraService.finalizarCotacao(idCompra);

        assertNotNull(compraDTO1);
        assertEquals(compra.getStatus(), statusCompra);

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarNaoFinalizarCotacoComSucesso () throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        CotacaoEntity cotacao = getCotacaoEntity();
        Set<CotacaoEntity> cotacaoEntities = new HashSet<>();
        cotacaoEntities.add(cotacao);
        compra.setCotacoes(cotacaoEntities);
        Integer idCompra = 10;

        when(compraRepository.findById(anyInt())).thenReturn(Optional.of(compra));

        compraService.finalizarCotacao(idCompra);
    }

    @Test
    public void deveTestarReprovarCompraGestorComSucesso() throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        compra.setStatus(StatusCompra.COTADO);
        CompraEntity compraRetorno = getCompraEntity();
        compraRetorno.setStatus(StatusCompra.REPROVADO_GESTOR);
        Integer idCompra = 10;

        when(compraServiceUtil.findByIDCompra(anyInt())).thenReturn(compra);
        when(compraRepository.save(any(CompraEntity.class))).thenReturn(compraRetorno);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        compraService.reprovarCompraGestor(idCompra);

        assertNotNull(compraRetorno);
        assertEquals(compraRetorno.getStatus(), StatusCompra.REPROVADO_GESTOR);
    }

    @Test(expected = EntidadeNaoEncontradaException.class)
    public void deveTestarReprovarCompraGestorSemCompra() throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        Integer idCompra = 10;

        doThrow(EntidadeNaoEncontradaException.class).when(compraServiceUtil).findByIDCompra(anyInt());

        compraService.reprovarCompraGestor(idCompra);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarReprovarCompraGestorOperacaoInvalida() throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        Integer idCompra = 10;

        when(compraServiceUtil.findByIDCompra(anyInt())).thenReturn(compra);

        compraService.reprovarCompraGestor(idCompra);
    }

    private static ItemUpdateDTO getItemUpdateDTO () {
        ItemUpdateDTO itemUpdateDTO = new ItemUpdateDTO();
        itemUpdateDTO.setIdItem(10);
        itemUpdateDTO.setNome("teste");
        itemUpdateDTO.setQuantidade(3);
        return itemUpdateDTO;
    }

    private static CompraUpdateDTO getCompraUpdateDTO () {
        CompraUpdateDTO compraUpdateDTO = new CompraUpdateDTO();
        compraUpdateDTO.setDescricao("teste");
        compraUpdateDTO.setName("teste");
        return compraUpdateDTO;
    }

    private static ItemValorizadoDTO getItemValorizadoDTO () {
        ItemValorizadoDTO itemValorizadoDTO = new ItemValorizadoDTO();
        itemValorizadoDTO.setIdItem(10);
        itemValorizadoDTO.setValorTotal(100.0);
        itemValorizadoDTO.setQuantidade(3);
        itemValorizadoDTO.setValorUnitario(10.0);
        itemValorizadoDTO.setNome("teste");
        return itemValorizadoDTO;
    }

    private static CompraWithValorItensDTO getCompraWithValorItensDTO () {
        CompraWithValorItensDTO compra1 = new CompraWithValorItensDTO();
        compra1.setIdCompra(10);
        compra1.setName("teste");
        compra1.setValor(10.0);
        compra1.setDescricao("teste");
        compra1.setStatus(StatusCompra.COTADO);
        return compra1;
    }

    private static CompraListDTO getCompraListDTO () {
        CompraListDTO compraListDTO = new CompraListDTO();
        compraListDTO.setIdCompra(10);
        compraListDTO.setStatus("teste");
        compraListDTO.setName("teste");
        compraListDTO.setValorTotal(100.0);
        return compraListDTO;
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
        return cotacao;
    }

    private static ItemEntity getItemEntity () {
        ItemEntity item = new ItemEntity();
        item.setIdItem(10);
        item.setCompra(null);
        item.setNome("item");
        item.setCotacoes(null);
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

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setIdItem(12);
        itemEntity.setNome("batata");
        itemEntity.setQuantidade(3);

        compra.setItens(Set.of(itemEntity));
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
        usuario.setIdUser(10);
        usuario.setEmail("teste@bdccompany.com.br");
        return usuario;

    }
}