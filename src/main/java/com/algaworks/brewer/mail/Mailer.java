package com.algaworks.brewer.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;

@Component
public class Mailer {
	
	@Autowired
	private JavaMailSender sender;
	
	@Autowired
	private TemplateEngine templateEngineThymeleaf;
	
	private static final Logger logger = LoggerFactory.getLogger(Mailer.class);
	
	@Autowired
	private FotoStorage fotoStorage;
	
	//Indica que a chamada será assincrona
	// para isso tem que configurar no webconfig
	@Async
	public void enviarTeste() {
		System.out.println(">> Email enviando!!");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(">> Email enviado!!");
	}
	
	@Async
	public void enviarTesteComSendGrid(Venda venda) {
		System.out.println(">> Email enviando!!");
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setFrom("joaovictor2@hotmail.com");
		mail.setTo(venda.getCliente().getEmail());
		mail.setSubject(String.format("Brewer - Venda Efetuada nº %d", venda.getCodigo()));
		mail.setText("Sua venda foi processada!! Obrigado pela preferência");
		
		sender.send(mail);
		
		System.out.println(">> Email enviado!!");
	}
	
	@Async
	public void enviar(Venda venda) {
		
		Context context = new Context(new Locale("pt","BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo");
		
		String fotoCerveja = null;

		Map<String, String> fotos = new HashMap<>();
		Boolean adicionarCerveja = false;
		
		for (ItemVenda item : venda.getItens()) {
			Cerveja cerveja = item.getCerveja();
			
			if(!StringUtils.isEmpty(cerveja.getFoto())) {
				String cid = "foto-" + cerveja.getCodigo();
				context.setVariable(cid, cid);
				
				fotos.put(cid, cerveja.getFoto() + "|" + cerveja.getContentType());
			} else {
				adicionarCerveja = true;
				context.setVariable("mockCerveja", "mockCerveja");
			}
			
		}
		
		try {
			String email = templateEngineThymeleaf.process("mail/ResumoVenda", context);
			MimeMessage mimeMessage = sender.createMimeMessage();
			//true para enviar muiltpart (imagens)
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			mimeMessageHelper.setFrom("joaovictor2@hotmail.com");
			mimeMessageHelper.setTo(venda.getCliente().getEmail());
			mimeMessageHelper.setSubject(String.format("Brewer - Venda Efetuada nº %d", venda.getCodigo()));
			mimeMessageHelper.setText(email, true);
			mimeMessageHelper.addInline("logo", new ClassPathResource("static/layout/images/logo-gray.png"));
			
			if(adicionarCerveja) {
				mimeMessageHelper.addInline("mockCerveja", new ClassPathResource("static/layout/images/cerveja-mock.png"));
			}
			
			for (String cid : fotos.keySet()) {
				String[] fotoContentType = fotos.get(cid).split("\\|");
				String foto = fotoContentType[0];
				String contentType = fotoContentType[1];
				byte[] arrayFoto = fotoStorage.recuperarThumbnail(foto);
				mimeMessageHelper.addInline(cid, new ByteArrayResource(arrayFoto), contentType);
			}
			
			sender.send(mimeMessage);
		} catch (MessagingException e) {
			logger.error("Erro enviando email", e);
		}
		
		System.out.println(">> Email enviado!!");
	}
}
