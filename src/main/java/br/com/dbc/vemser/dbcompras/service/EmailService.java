package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final freemarker.template.Configuration fmConfiguration;

    @Value("${spring.mail.username}")
    private String from;
    private String mensagem;

    private final JavaMailSender emailSender;


    public void sendEmail(String nome, String compra, String email, StatusCompra statusCompra) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(email);
            switch (statusCompra){
                case ABERTO -> {
                    mimeMessageHelper.setSubject("Solicitação de compra recebida");
                }
                case APROVADO_GESTOR -> {
                    mimeMessageHelper.setSubject("Solicitação de compra aprovada - gestor");
                }
                case REPROVADO_GESTOR -> {
                    mimeMessageHelper.setSubject("Solicitação de compra reprovada - gestor");
                }
                case APROVADO_FINANCEIRO -> {
                    mimeMessageHelper.setSubject("Solicitação de compra aprovada - financeiro");
                }
                case REPROVADO_FINANCEIRO -> {
                    mimeMessageHelper.setSubject("Solicitação de compra reprovada - financeiro");
                }
                default -> mimeMessageHelper.setSubject("Compra finalizada com sucesso");
            }
            mimeMessageHelper.setText(geContentFromTemplate(nome, compra, email, statusCompra), true);

            emailSender.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException | IOException | TemplateException e) {
            mensagem = "Exception verificada";
        }
    }

    public String geContentFromTemplate(String nome, String compra, String email, StatusCompra status) throws IOException, TemplateException {
        Map<String, Object> dados = new HashMap<>();

        // local
        dados.put("email", from);

        // da requisicao
        dados.put("nome", nome);
        dados.put("emailRequisicao", email);
        dados.put("compra", compra);


        String html = null;
        switch (status){
            case ABERTO -> {
                Template template = fmConfiguration.getTemplate("nova-compra-template.html");
                html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
            }
            case REPROVADO_GESTOR, REPROVADO_FINANCEIRO -> {
                Template template = fmConfiguration.getTemplate("solicitacao-reprovada.html");
                html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
            }
            case APROVADO_GESTOR, APROVADO_FINANCEIRO -> {
                Template template = fmConfiguration.getTemplate("solicitacao-aprovada.html");
                html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
            }
            case FECHADO -> {
                Template template = fmConfiguration.getTemplate("edit-email-template.ftl");
                html = FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
            }
        }

        return html;
    }

    public String getMensagem() {
        return mensagem;
    }

}
