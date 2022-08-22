package br.com.dbc.vemser.dbcompras.service;

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
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;
    private String mensagem = "";


    public void sendEmail(String nome, String compra, String email, String statusCompra) {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        try {

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(email);
            switch (statusCompra){
                case "ABERTO" -> {
                    mimeMessageHelper.setSubject("Solicitação de compra recebida");
                }
                case "APROVADO_GESTOR" -> {
                    mimeMessageHelper.setSubject("Solicitação de compra aprovada - gestor");
                }
                case "REPROVADO_GESTOR" -> {
                    mimeMessageHelper.setSubject("Solicitação de compra reprovada - gestor");
                }
                case "APROVADO_FINANCEIRO" -> {
                    mimeMessageHelper.setSubject("Solicitação de compra aprovada - financeiro");
                }
                case "REPROVADO_FINANCEIRO" -> {
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

    public String geContentFromTemplate(String nome, String compra, String email, String status) throws IOException, TemplateException {
        Map<String, Object> dados = new HashMap<>();

        // local
        dados.put("email", from);

        // da requisicao
        dados.put("nome", nome);
        dados.put("emailRequisicao", email);
        dados.put("compra", compra);


        Template template = null;
        switch (status){
            case "REPROVADO_GESTOR", "REPROVADO_FINANCEIRO" -> {
                template = fmConfiguration.getTemplate("solicitacao_reprovada-template.ftl");
            }
            case "APROVADO_GESTOR", "APROVADO_FINANCEIRO" -> {
                template = fmConfiguration.getTemplate("solicitacao_aprovada-template.ftl");
            }
            case "FECHADO" -> {
                template = fmConfiguration.getTemplate("compra_finalizada-template.ftl");
            }
            default -> {
                template = fmConfiguration.getTemplate("nova_compra-template.ftl");
            }
        }

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, dados);
    }

    public String getMensagem() {
        return mensagem;
    }

}
