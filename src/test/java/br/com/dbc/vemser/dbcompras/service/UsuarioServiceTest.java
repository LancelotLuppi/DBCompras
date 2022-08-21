package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.*;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.ControlarAcesso;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
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
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private UsuarioServiceUtil usuarioServiceUtil;

    @Mock
    private Argon2PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(usuarioService, "objectMapper", objectMapper);
    }

    @Test
    public void deveTestarCriarUsuarioComSucesso() throws RegraDeNegocioException {

        // setup
        UsuarioEntity usuario = getUsuarioEntity();
        CargoEntity cargo = getCargoEntity();
        usuario.setCargos(Set.of(cargo));
        UserCreateDTO userCreateDTO = getUsuarioCreateDTO();
        UserLoginComSucessoDTO user = new UserLoginComSucessoDTO();
        user.setEmail("teste@bdccompany.com.br");
        user.setNome("Rodrigo");
        // act
        when(usuarioServiceUtil.retornarUsuarioEntity(any(UserCreateDTO.class))).thenReturn(usuario);
        when(cargoRepository.findById(anyInt())).thenReturn(Optional.of(cargo));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(usuarioServiceUtil.generateUserLoginComSucessoDTO(any(UsuarioEntity.class), anyString(), anyString())).thenReturn(user);
        UserLoginComSucessoDTO usuarioDTO = usuarioService.create(userCreateDTO);
        // assert
        assertNotNull(usuarioDTO);
        assertEquals(usuarioDTO.getNome(), usuario.getNome());
        assertEquals(usuarioDTO.getEmail(), usuario.getEmail());

    }

    @Test
    public void deveTestarUpdateDoAdminComSucesso () throws RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        CargoEntity cargo = getCargoEntity();
        usuario.setCargos(Set.of(cargo));
        Set<TipoCargo> tipoCargos = new HashSet<>();
        tipoCargos.add(TipoCargo.ADMINISTRADOR);
        when(usuarioServiceUtil.findById(anyInt())).thenReturn(usuario);
        when(cargoRepository.findById(anyInt())).thenReturn(Optional.of(cargo));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);

        UserWithCargoDTO userDTO = usuarioService.updateUserByAdmin(usuario.getIdUser(), tipoCargos);

        assertNotNull(userDTO);
        assertEquals(userDTO.getIdUser(), usuario.getIdUser());
        assertEquals(userDTO.getEmail(), usuario.getEmail());
        assertEquals(userDTO.getNome(), usuario.getNome());

    }

    @Test
    public void deveTestarupdateLoggedUserComSucesso () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        CargoEntity cargo = getCargoEntity();
        usuario.setCargos(Set.of(cargo));
        UserDTO userDTO = getUserDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        doNothing().when(usuarioServiceUtil).validarEmail(anyString());
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(usuarioServiceUtil.retornarUsuarioDTO(any(UsuarioEntity.class))).thenReturn(userDTO);

        UserDTO userDTO1 = usuarioService.updateLoggedUser(getUserUpdateDTO());

        assertNotNull(userDTO1);
        assertEquals(userDTO1.getIdUser(), userDTO.getIdUser());
        assertEquals(userDTO1.getEmail(), userDTO.getEmail());
        assertEquals(userDTO1.getNome(), userDTO.getNome());
    }

    @Test
    public void deveTestarupdateEmailLoggedUserComSucesso () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        CargoEntity cargo = getCargoEntity();
        usuario.setCargos(Set.of(cargo));
        UserDTO userDTO = getUserDTO();
        UserUpdateDTO userUpdateDTO = getUserUpdateDTO();
        userUpdateDTO.setEmail("rodrigo@bdccompany.com.br");


        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        doNothing().when(usuarioServiceUtil).validarEmail(anyString());
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(usuarioServiceUtil.retornarUsuarioDTO(any(UsuarioEntity.class))).thenReturn(userDTO);

        UserDTO userDTO1 = usuarioService.updateLoggedUser(userUpdateDTO);

        assertNotNull(userDTO1);
        assertEquals(userDTO1.getIdUser(), userDTO.getIdUser());
        assertEquals(userDTO1.getEmail(), userDTO.getEmail());
        assertEquals(userDTO1.getNome(), userDTO.getNome());


    }

    @Test
    public void deveTestarDesativarContaLogadaComSucesso () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        UserLoginDTO userLoginDTO = getUserLoginDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(passwordEncoder.matches(anyString(), eq(usuario.getPassword()))).thenReturn(true);

        usuarioService.desativarContaLogada(userLoginDTO);

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveFalharAoDesativarContaLogadaComSucesso () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        UserLoginDTO userLoginDTO = getUserLoginDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);

        usuarioService.desativarContaLogada(userLoginDTO);

    }

    @Test
    public void deveDeletarUsuarioComSucesso () throws RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();

        when(usuarioServiceUtil.findById(anyInt())).thenReturn(usuario);
        doNothing().when(usuarioRepository).delete(any(UsuarioEntity.class));

        usuarioService.deletarUsuario(anyInt());

        verify(usuarioRepository, times(1)).delete(any(UsuarioEntity.class));

    }

    @Test
    public void deveTestarCriarUsuarioByAdminComSucesso() throws RegraDeNegocioException {

        // setup
        UsuarioEntity usuario = getUsuarioEntity();
        CargoEntity cargo = getCargoEntity();
        usuario.setCargos(Set.of(cargo));
        Set<TipoCargo> tipoCargos = new HashSet<>();
        tipoCargos.add(TipoCargo.COLABORADOR);
        UserCreateDTO userCreateDTO = getUsuarioCreateDTO();
        UserCreateByAdminDTO user = new UserCreateByAdminDTO();
        user.setEmail("teste@bdccompany.com.br");
        user.setNome("Rodrigo");
        UserUpdateByAdminDTO userUpdateByAdminDTO = getUserUpdateByAdminDTO();
        // act
        when(usuarioServiceUtil.retornarUsuarioEntity(any(UserCreateDTO.class))).thenReturn(usuario);
        when(cargoRepository.findById(anyInt())).thenReturn(Optional.of(cargo));
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(usuarioServiceUtil.findById(anyInt())).thenReturn(usuario);
        when(usuarioServiceUtil.retornarUsuarioCriadoDTO(any(UsuarioEntity.class))).thenReturn(user);

        UserCreateByAdminDTO usuarioDTO = usuarioService.createUserByAdmin(userCreateDTO, tipoCargos);
        // assert
        assertNotNull(usuarioDTO);
        assertEquals(usuarioDTO.getNome(), usuario.getNome());
        assertEquals(usuarioDTO.getEmail(), usuario.getEmail());

    }

    @Test
    public void deveTestarRetornarUsuarioLogadoComSucesso () throws UsuarioException {

        UsuarioEntity usuario = getUsuarioEntity();
        UserWithCargoDTO userWithProfileImageDTO = getUserWithCargoDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        when(usuarioServiceUtil.retornarUsuarioDTOComCargo(any(UsuarioEntity.class))).thenReturn(userWithProfileImageDTO);

        UserWithCargoDTO userDTO = usuarioService.getLoggedUser();

        assertNotNull(userDTO);
        assertEquals(userDTO.getIdUser(), usuario.getIdUser());
        assertEquals(userDTO.getEmail(), usuario.getEmail());
        assertEquals(userDTO.getNome(), usuario.getNome());

    }

    @Test
    public void deveValidarLoginComSucesso () throws RegraDeNegocioException, UsuarioException {

        UserLoginDTO userLoginDTO = getUserLoginDTO();
        UsuarioEntity usuario = getUsuarioEntity();
        String token = "token";

        when(usuarioServiceUtil.recuperarToken(eq(usuario.getEmail()), eq(usuario.getPassword()))).thenReturn(token);

        String teste = usuarioService.validarLogin(userLoginDTO);

        assertNotNull(teste);

    }

    @Test
    public void deveListarUsuariosComSucesso () {

        List<UsuarioEntity> list = List.of(getUsuarioEntity());
        UserWithCargoDTO user = getUserWithCargoDTO();

        when(usuarioRepository.findAll()).thenReturn(list);
        when(usuarioServiceUtil.retornarUsuarioDTOComCargo(any(UsuarioEntity.class))).thenReturn(user);

        List<UserWithCargoDTO> dtoList = usuarioService.list();

        assertNotNull(dtoList);
        assertFalse(dtoList.isEmpty());

    }

    @Test
    public void deveTestarcontrolarAcessoUsuarioComSucesso () throws RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        ControlarAcesso controlarAcesso = ControlarAcesso.DESATIVAR;

        when(usuarioServiceUtil.findById(anyInt())).thenReturn(usuario);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);

        usuarioService.controlarAcessoUsuario(usuario.getIdUser(), controlarAcesso);

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));

    }

    @Test
    public void deveTestarAtualizarCredenciais () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        UserUpdatePasswordDTO user = new UserUpdatePasswordDTO();
        user.setNovaSenha("teste");
        user.setSenhaAtual("teste");
        boolean verificacao = true;
        String teste = "teste";
        UserDTO userDTO = getUserDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        when(usuarioServiceUtil.verificarSenhaUsuario(eq(user.getSenhaAtual()), eq(usuario))).thenReturn(verificacao);
        doNothing().when(usuarioServiceUtil).validarFormatacaoSenha(eq(user.getNovaSenha()));
        when(usuarioServiceUtil.encodePassword(eq(user.getNovaSenha()))).thenReturn(teste);
        when(usuarioRepository.save(any(UsuarioEntity.class))).thenReturn(usuario);
        when(usuarioServiceUtil.retornarUsuarioDTO(any(UsuarioEntity.class))).thenReturn(userDTO);

        usuarioService.updateLoggedPassword(user);

        verify(usuarioRepository, times(1)).save(any(UsuarioEntity.class));

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveNaoTestarAtualizarCredenciais () throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = getUsuarioEntity();
        UserUpdatePasswordDTO user = new UserUpdatePasswordDTO();
        user.setNovaSenha("teste");
        user.setSenhaAtual("teste");
        boolean verificacao = false;
        String teste = "teste";
        UserDTO userDTO = getUserDTO();

        when(usuarioServiceUtil.retornarUsuarioEntityLogado()).thenReturn(usuario);
        when(usuarioServiceUtil.verificarSenhaUsuario(eq(user.getSenhaAtual()), eq(usuario))).thenReturn(verificacao);
        usuarioService.updateLoggedPassword(user);

    }



    private UserWithCargoDTO getUserWithCargoDTO() {
        UserWithCargoDTO user = new UserWithCargoDTO();
        user.setIdUser(10);
        user.setEmail("teste@bdccompany.com.br");
        user.setNome("Rodrigo");
        return user;
    }

    private static UserLoginDTO getUserLoginDTO() {
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setEmail("teste@bdccompany.com.br");
        userLoginDTO.setPassword("AtackOnT1t@n");
        return userLoginDTO;
    }

    private static UserDTO getUserDTO (){
        UserDTO userDTO = new UserDTO();
        userDTO.setIdUser(10);
        userDTO.setEmail("teste@bdccompany.com.br");
        userDTO.setNome("Rodrigo");
        return userDTO;
    }

    private static UserUpdateDTO getUserUpdateDTO () {

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("teste@bdccompany.com.br");
        userUpdateDTO.setNome("Rodrigo");
        userUpdateDTO.setFoto("foto");
        return userUpdateDTO;

    }

    private static UserUpdateByAdminDTO getUserUpdateByAdminDTO () {
        UserUpdateByAdminDTO usuarioDTO = new UserUpdateByAdminDTO();
        usuarioDTO.setIdUser(10);
        usuarioDTO.setEmail("teste@bdccompany.com.br");
        usuarioDTO.setFoto("foto");
        usuarioDTO.setNome("Rodrigo");
        return usuarioDTO;
    }

    private static UserCreateDTO getUsuarioCreateDTO() {
        UserCreateDTO usuario = new UserCreateDTO();
        usuario.setEmail("teste@bdccompany.com.br");
        usuario.setNome("Rodrigo");
        usuario.setFoto("foto");
        usuario.setSenha("AtackOnT1t@n");
        return usuario;

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

    private static ItemEntity getItemEntity () {
        ItemEntity item = new ItemEntity();
        item.setIdItem(10);
        item.setCompra(getCompraEntity());
        item.setNome("item");
        item.setCotacoes(Set.of(getCotacaoXItem()));
        return item;
    }

    private static CotacaoEntity getCotacaoEntity () {
        CotacaoEntity cotacao = new CotacaoEntity();
        cotacao.setIdCotacao(10);
        cotacao.setUsuario(getUsuarioEntity());
        cotacao.setItens(Set.of(getCotacaoXItem()));
        cotacao.setCompra(getCompraEntity());
        cotacao.setLocalDate(LocalDateTime.now());
        cotacao.setStatus(StatusCotacao.EM_ABERTO);
        cotacao.setValor(10.0);
        cotacao.setAnexo(getUsuarioEntity().getPhoto());
        return cotacao;
    }

    private static CotacaoXItemEntity getCotacaoXItem () {
        CotacaoXItemEntity cotacaoXItem = new CotacaoXItemEntity();
        cotacaoXItem.setCotacao(getCotacaoEntity());
        cotacaoXItem.setItem(getItemEntity());
        cotacaoXItem.setValorDoItem(10.0);
        cotacaoXItem.setValorTotal(10.0);
        cotacaoXItem.setCotacaoXItemPK(getCotacaoXItemPK());
        return cotacaoXItem;
    }

    private static CotacaoXItemPK getCotacaoXItemPK () {
        CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
        cotacaoXItemPK.setIdCotacao(10);
        cotacaoXItemPK.setIdItem(10);
        return cotacaoXItemPK;
    }

    private static CargoEntity getCargoEntity () {
        CargoEntity cargo = new CargoEntity();
        cargo.setIdCargo(10);
        cargo.setName("teste");
        cargo.setUsuarios(Set.of(getUsuarioEntity()));
        return cargo;
    }

}
