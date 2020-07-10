package me.zhulin.shopapi.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;



import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import me.zhulin.shopapi.entity.OrderMain;

@Service
public class EmailService {
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private Configuration config;
	@Autowired
	EmailServiceMessage service1;
	
	public void sendEmail(OrderMain order, Map<String, Object> model) {
		
		MimeMessage message = sender.createMimeMessage();
		try {
			// set mediaType
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			// add attachment
			

			Template t = config.getTemplate("email-template.ftl");
			String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);

			helper.setTo(order.getBuyerEmail());
		 //   helper.setTo("ghorai77@gmail.com");
			helper.setText(html, true);
			helper.setSubject(order.getBuyerAddress());
			helper.setFrom("ghorai77@gmail.com");
			sender.send(message);
        
			 service1.sendEmail1(order, model);

		} catch (MessagingException | IOException | TemplateException e) {
			
		}

		
	}
	

}
