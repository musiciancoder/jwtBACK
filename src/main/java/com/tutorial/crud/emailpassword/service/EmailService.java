package com.tutorial.crud.emailpassword.service;



import com.tutorial.crud.emailpassword.dto.EmailValuesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Value("${mail.urlFront}")
    private String urlFront;

/*    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("andresguitarra1980@gmail.com"); //quien lo esta enviando (debe ser el valor de application.properties)
        message.setTo("andresguitarra1980@gmail.com"); //enviar hacia mi mismo
        message.setSubject("Prueba envío email simple");
        message.setText("Esto es el contenido el mail");
        javaMailSender.send(message);
    }*/

    //Esto es para incluir la plantilla de thymeleaf
    public void sendEmail(EmailValuesDTO dto) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            Map<String, Object> model = new HashMap<>();
            model.put("userName", dto.getUserName()); //lo obtiene de lo q se llena en el formulario
            model.put("url", urlFront + dto.getTokenPassword());
            context.setVariables(model);
            String htmlText = templateEngine.process("email-context",context);
            helper.setFrom("andresguitarra1980@@gmail.com"); //quien lo esta enviando (debe ser el valor de application.properties)
            helper.setTo("andresguitarra1980@@gmail.com"); //enviar hacia mi mismo
            helper.setSubject("Prueba envio email simple");
            helper.setText(htmlText, true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
