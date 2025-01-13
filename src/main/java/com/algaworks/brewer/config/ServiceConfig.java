package com.algaworks.brewer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.algaworks.brewer.controller.storage.local.FotoStorageLocal;
import com.algaworks.brewer.service.CervejaService;

//FotoStorage está sendo scaniado devido a mudança de armazenamento para o S3
@Configuration
@ComponentScan(basePackageClasses = {CervejaService.class, FotoStorage.class})
//@ComponentScan(basePackageClasses = CervejaService.class)
public class ServiceConfig {
	
	/*@Bean
	public FotoStorage fotoStorage() {
		return new FotoStorageLocal();
	}*/
	
}
