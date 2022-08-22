package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import br.com.dbc.vemser.dbcompras.security.TokenService;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UsuarioServiceUtilsTest {

    @InjectMocks
    private UsuarioServiceUtil usuarioServiceUtil;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private TokenService tokenService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationManager authenticationManager;
    private Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();

    @Before
    public void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ReflectionTestUtils.setField(usuarioServiceUtil, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(usuarioServiceUtil, "passwordEncoder", passwordEncoder);
    }

    @Test
    public void deveTestarFindByIdComSucesso () throws RegraDeNegocioException {
        UsuarioEntity usuario = getUsuarioEntity();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));

        UsuarioEntity usuario1 = usuarioServiceUtil.findById(usuario.getIdUser());

        assertNotNull(usuario1);
        assertEquals(usuario.getEmail(), usuario1.getEmail());
        assertEquals(usuario.getIdUser(), usuario1.getIdUser());
        assertEquals(usuario.getNome(), usuario1.getNome());

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarFindByIdSemSucesso () throws RegraDeNegocioException {
        UsuarioEntity usuario = getUsuarioEntity();

        UsuarioEntity usuario1 = usuarioServiceUtil.findById(usuario.getIdUser());

    }

    @Test
    public void deveTestaRetornarUsuarioEntityLogado () throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = getUsuarioEntity();
        criarUsuarioLogado();

        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuario));

        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntityLogado();

        assertNotNull(usuarioEntity);

    }

    @Test
    public void deveTestarBuscarIdDoUsuarioLogado () throws UsuarioException {

        Integer idUser = 10;
        criarUsuarioLogado();

        Integer idUserEntity = usuarioServiceUtil.getIdLoggedUser();

        assertNotNull(idUserEntity);

    }

    @Test
    public void deveTestarValidarEmailComSucesso () throws RegraDeNegocioException {

        String email = "teste@dbccompany.com.br";

        usuarioServiceUtil.validarEmail(email);

    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarNaoValidarEmailComSucesso () throws RegraDeNegocioException {

        String email = "teste@gmail.com.br";

        usuarioServiceUtil.validarEmail(email);

    }

    @Test
    public void deveValidarFormatacaoDeSenha() throws RegraDeNegocioException {
        String senha = "Ir@nmam98";

        usuarioServiceUtil.validarFormatacaoSenha(senha);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveNaoValidarFormatacaoDeSenha() throws RegraDeNegocioException {
        String senha = "Ir@nmam";

        usuarioServiceUtil.validarFormatacaoSenha(senha);
    }

    @Test
    public void deveVerificarSenhaDoUsuario () throws UsuarioException {

        boolean teste = false;

        String testeSenha = "AtackOnT1t@n";
        UsuarioEntity usuario = getUsuarioEntity();

        boolean resultado = usuarioServiceUtil.verificarSenhaUsuario(testeSenha,usuario);

        assertEquals(resultado, teste);

    }


    private void criarUsuarioLogado() {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        123,
                        null
                );
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
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
