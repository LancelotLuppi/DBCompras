package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private freemarker.template.Configuration fmConfiguration;

    @Test
    public void deveTestarSendEmailCompraCriar() throws IOException, MessagingException, TemplateException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.ABERTO;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "createee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void deveTestarSendEmailAprovarCompra() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.APROVADO_FINANCEIRO;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "updateee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void deveTestarSendEmailReprovarCompra() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.REPROVADO_FINANCEIRO;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "updateee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }


    @Test
    public void deveTestarSendEmailReprovarCompraGestor() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.REPROVADO_GESTOR;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "updateee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }


    @Test
    public void deveTestarSendEmailCompraFechada() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.FECHADO;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "updateee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void deveTestarSendEmailAprovadoGestor() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.APROVADO_GESTOR;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "deleteee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void deveTestarSendEmailDefault() throws IOException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.COTADO;
        CompraEntity compra = getCompraEntity();

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "mr_robot@testedostestes.com");
        doNothing().when(emailSender).send(any(MimeMessage.class));
        when(fmConfiguration.getTemplate(anyString())).thenReturn(new Template("Oiii", "deleteee", new Configuration()));

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        verify(emailSender, times(1)).send(any(MimeMessage.class));
    }



    @Test
    public void deveTestarSendEmailSemSucesso() throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = getUsuarioEntity();
        StatusCompra statusCompra = StatusCompra.ABERTO;
        CompraEntity compra = getCompraEntity();
        String mensagemTest = "Exception verificada";

        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(emailService, "from", "");

        emailService.sendEmail(usuarioEntity.getNome(), compra.getName(), usuarioEntity.getEmail(), statusCompra.getStatusCompra());

        assertEquals(mensagemTest, emailService.getMensagem());
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

}
