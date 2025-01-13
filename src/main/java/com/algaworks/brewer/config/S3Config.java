package com.algaworks.brewer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.algaworks.brewer.controller.storage.FotoStorageRunnable;
import com.algaworks.brewer.controller.storage.s3.FotoStorageS3;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
//@ComponentScan(basePackageClasses = {FotoStorageRunnable.class, FotoStorage.class,  FotoStorageS3.class})
@Configuration
@PropertySource(value={"file:${HOME}/.brewer-s3.properties"}, ignoreResourceNotFound = false)
public class S3Config {
	
	@Autowired
	private Environment env;
	
	@Bean
	public AmazonS3 amazons3() {
		System.out.println(env.getProperty("AWS_ACCESSKEY_ID"));
		System.out.println(env.getProperty("AWS_SECRET_KEY"));
		AWSCredentials credencials =  new BasicAWSCredentials(env.getProperty("AWS_ACCESSKEY_ID"), 
				                                                   env.getProperty("AWS_SECRET_KEY"));
		AmazonS3 amazonS3 = new AmazonS3Client(credencials, new ClientConfiguration());
		amazonS3.setRegion(Region.getRegion(Regions.US_EAST_1));
		return amazonS3;
	}
}
