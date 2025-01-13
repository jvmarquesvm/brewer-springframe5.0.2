package com.algaworks.brewer.config.init;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.algaworks.brewer.config.JPAConfig;
import com.algaworks.brewer.config.MailConfig;
import com.algaworks.brewer.config.S3Config;
import com.algaworks.brewer.config.SecurityConfig;
import com.algaworks.brewer.config.ServiceConfig;
import com.algaworks.brewer.config.WebConfig;

public class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?> [] { JPAConfig.class, 
			                        ServiceConfig.class, SecurityConfig.class,
			                            S3Config.class     };
	}

	/**
	 * Configuração para o Spring encontrar os Controllers 
	 * 
	 * Inicilizar a configuração de email
	 */
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class, MailConfig.class };
	}
	
	/**
	 * Configurando que qualquer url dentro da aplicação será mapeado pelo
	 * Dispatcher Servlet
	 * url mappings no xml
	 * 
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	/**
	 * Resolvendo problemas de acentuação
	 */
	@Override
	protected Filter[] getServletFilters() {
		
		//Após a configuração o Spring Security, a ordem dos filtros foram modificados 
		// e esta configuração não funciona mais, é necessário passar este código para 
		// antes da configuração de segurança
		// Configuração de acentuação sendo feito no SecurityInitalizer
		
		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);
		
		//Configuração para aceitar PUT
		HttpPutFormContentFilter httpPutFormContentFilter = new HttpPutFormContentFilter();
		return new Filter[] { httpPutFormContentFilter, encodingFilter };
	}
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
		super.customizeRegistration(registration);
		registration.setMultipartConfig(new MultipartConfigElement(""));
	}
	
	//Configuração para usar ambiente local - quando implementou o S3 local ou prod
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext);
		servletContext.setInitParameter("spring.profiles.default", "local");
	}

}
