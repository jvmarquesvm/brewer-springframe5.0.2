package com.algaworks.brewer.config;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import javax.cache.Caching;
import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.jcache.JCacheCacheManager;
//import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import org.springframework.web.servlet.resource.ResourceResolver;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsMultiFormatView;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;
//import org.springframework.web.servlet.view.jasperreports.JasperReportsViewResolver;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.algaworks.brewer.config.format.BigDecimalFormatter;
import com.algaworks.brewer.controller.CervejasController;
import com.algaworks.brewer.controller.converter.CidadeConverter;
import com.algaworks.brewer.controller.converter.EstadoConverter;
import com.algaworks.brewer.controller.converter.EstiloConverter;
import com.algaworks.brewer.controller.converter.GrupoConverter;
import com.algaworks.brewer.session.TabelasItensSession;
import com.algaworks.brewer.thymeleaf.processor.BrewerDialect;
import com.algaworks.brewer.thymeleaf.processor.MessageElementTagProcessor;
import com.github.mxab.thymeleaf.extras.dataattribute.dialect.DataAttributeDialect;
//import com.google.common.cache.CacheBuilder;

import nz.net.ultraq.thymeleaf.LayoutDialect;

/**
 * @author joaovictor
 * 
 * Configuração para o Spring encontrar os Controllers
 * 1 - forma: Chamando diretamente o controller
 * @ComponentScan("com.algaworks.brewer.controller")
 * 2 - forma: Tendo como base o pacote da classe
 * @ComponentScan( basePackageClasses = {CervejasController.class})
 * 3 - Habilita configurações para WebMvc
 * @EnableWebMvc
 * 4 - Herdar as configurações de Adapter e habilita o spring mvc
 * extends WebMvcConfigurerAdapter {
 * 
 * @EnableSpringDataWebSupport
 * Adiciona suporte a alguma coisa do spring data para parte web ( instanciar o pageable )
 * 
 * @EnableCaching
 * Adiciona suporte a cache na aplicação
 * 
 * @EnableAsync
 * Permite enviar chamadas assincronas( configuração de email )
 * 
 */

@Configuration            //utilizandoSession pra simular escopo de view para a tabela de itens de venda
@ComponentScan(basePackageClasses = {CervejasController.class, TabelasItensSession.class})
@EnableWebMvc
@EnableSpringDataWebSupport
@EnableCaching
@EnableAsync
//Removido na versão 5.0.2
//public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {
public class WebConfig implements ApplicationContextAware, WebMvcConfigurer {
	private ApplicationContext applicationContext;
	
	private ITemplateResolver templateResolver() {
		SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("classpath:/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode(TemplateMode.HTML);
		return resolver;
	}
	
	@Bean
	public TemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(true);
		engine.setTemplateResolver(templateResolver());
		engine.addDialect(new LayoutDialect()); //configurando o dialeto thymeleaf para o decorator
		engine.addDialect(new BrewerDialect()); //Extendendo o thymeleaf
		engine.addDialect(new DataAttributeDialect()); //Extendendo o thymeleaf - atributos data
		engine.addDialect(new SpringSecurityDialect()); //Extendendo o thymeleaf - Spring Security ( para buscar o nome do usuário logado )
		return engine;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Bean
	public ViewResolver viewResolver() {
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		resolver.setCharacterEncoding("UTF-8");
		resolver.setOrder(1);
		return resolver;
	}
	
	/**
	 * Adicionar recursos que não tem na aplicação
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
	}
	
	@Bean
	public FormattingConversionService mvcConversionService() {
		DefaultFormattingConversionService service = new DefaultFormattingConversionService();
		//Adiconando os coverters - modificando o tipo do identificado de String para Long
		service.addConverter(new EstiloConverter());
		service.addConverter(new CidadeConverter());
		service.addConverter(new EstadoConverter());
		service.addConverter(new GrupoConverter());
		
		//Utiliza o locale que foi enviado pelo cliente
		//Se for usar um formatador diferente, tem que criar um
		//NumberStyleFormatter bigDecimalFormatter = new NumberStyleFormatter("#,##0.00");
		BigDecimalFormatter bigDecimalFormatter = new BigDecimalFormatter("#,##0.00");
		service.addFormatterForFieldType(BigDecimal.class, bigDecimalFormatter);
		
		//NumberStyleFormatter integerFormatter = new NumberStyleFormatter("#,##0");
		BigDecimalFormatter integerFormatter = new BigDecimalFormatter("#,##0");
		service.addFormatterForFieldType(Integer.class, integerFormatter);
		
		//API de datas >= Java8
		DateTimeFormatterRegistrar dateTimeFormatterRegistrar = new DateTimeFormatterRegistrar();
		dateTimeFormatterRegistrar.setDateFormatter(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		
		//Adicionando para a tabela venda hora da entrega
		dateTimeFormatterRegistrar.setTimeFormatter(DateTimeFormatter.ofPattern("HH:mm"));
		dateTimeFormatterRegistrar.registerFormatters(service);
		
		return service;
	}
	
	//Esta forçando que o idioma pt seja sempre usado no navegador e para formatação numérica
	//para internacionalização isto não funciona
	//@Bean
	//public LocaleResolver localeResolver() {
	//	return new FixedLocaleResolver(new Locale("pt", "BR"));
	//}
	
	/*
	 * Cache simples baseado em mapas - para uso local
	 * Na versão 5.0.2 será utilizado a implementação oficial de cache
	 * ehCache
	 * */
//	@Bean
//	public CacheManager cacheManager() {
//		return new ConcurrentMapCacheManager();
//	}
	
	@Bean
	public CacheManager cacheManager() throws Exception {
		return new JCacheCacheManager(Caching.getCachingProvider().getCacheManager(
				getClass().getResource("/cache/ehcache.xml").toURI(), 
				getClass().getClassLoader()
			));
	}
	
	/*
	 * Configurando o Cache do Guava
	 * Contem mais recursos de configuração
	 * 
	 * Guava removido por não ter suporte na versão 5.0.2 do Springframework
	 * */
//	@Bean
//	public CacheManager cacheManager() {
//		CacheBuilder<Object, Object> cacheBuilder = 
//				      CacheBuilder.newBuilder()
//				                  .maximumSize(2)
//				                  .expireAfterAccess(20, TimeUnit.SECONDS);
//		
//		GuavaCacheManager cacheManager = new GuavaCacheManager();
//		cacheManager.setCacheBuilder(cacheBuilder);
//		return cacheManager;
//	}
	
	
	//Colocado para traduzir o erro de datas mal formatada
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource bundle = new ReloadableResourceBundleMessageSource();
		bundle.setBasename("classpath:/messages");
		bundle.setDefaultEncoding("UTF-8"); //http://www.utf8-chartable.de
		
		return bundle;
	}
	
	//Integração entre o MVC e SpringData - com isso já não é necessário fazer o findOne
	@Bean
	public DomainClassConverter<FormattingConversionService> domainClassConverter() {
		return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
	}
	
	//Configuração para validação de mensagens de aviso
	//Verificar arquivo de properties do hibernate validator
	//As chaves estão lá
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
		localValidatorFactoryBean.setValidationMessageSource(messageSource());
		return localValidatorFactoryBean;
	}

	public Validator getValidator() {
		return validator();
	}
	
	//Criando ViewResolver para a página de relatorio com Jasper
	//Na versão 5.0.2 do Springframework não tem suporte ao jasperReportView
//	@Bean
//	public ViewResolver jasperReportsViewResolver(DataSource dataSource) {
//		JasperReportsViewResolver resolver = new JasperReportsViewResolver();
//		resolver.setPrefix("classpath:/relatorios/");
//		resolver.setSuffix(".jasper");
//		resolver.setViewNames("relatorio_*");
//		resolver.setViewClass(JasperReportsMultiFormatView.class);
//		resolver.setJdbcDataSource(dataSource);
//		resolver.setOrder(0);
//		return resolver;
//	}
}
