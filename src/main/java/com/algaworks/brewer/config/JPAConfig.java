package com.algaworks.brewer.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.CervejasRepository;

@Configuration
@EnableJpaRepositories(basePackageClasses = CervejasRepository.class, enableDefaultTransactions = false) //, repositoryImplementationPostfix = "Impl" )
//@ComponentScan(basePackageClasses = CervejaRepository.class)
@ComponentScan(basePackageClasses = {CervejasRepository.class})
@EnableTransactionManagement
public class JPAConfig {
	
	@Profile("local")
	@Bean
	public DataSource dataSource() {
		JndiDataSourceLookup dataSource = new JndiDataSourceLookup();
		dataSource.setResourceRef(true);
		return dataSource.getDataSource("jdbc/brewerDB");
	}
	
	@Profile("prod")
	@Bean
	public DataSource dataSourceProd() throws URISyntaxException {
		URI jdbUri = new URI(System.getenv("JAWSDB_URL"));

		String username = jdbUri.getUserInfo().split(":")[0];
		String password = jdbUri.getUserInfo().split(":")[1];
		String port = String.valueOf(jdbUri.getPort());
		String jdbUrl = "jdbc:mysql://" + jdbUri.getHost() + ":" + port + jdbUri.getPath();

		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(jdbUrl);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setInitialSize(10);
		
		return dataSource;
	}
	
	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(true);
		adapter.setGenerateDdl(false);
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
		return adapter;
	}
	
	@Bean
	public EntityManagerFactory entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource);
		factory.setJpaVendorAdapter(jpaVendorAdapter);
		factory.setPackagesToScan(Cerveja.class.getPackage().getName());
		//factory.setPackagesToScan("com.algaworks.brewer.model");
		factory.setPersistenceUnitName("PUBrewer");
		//Indicando ao JPA onde encontrar o arquivo externo de consultas sql
		factory.setMappingResources("sql/Consultas-Nativas.xml");
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		JpaTransactionManager transctionManager = new JpaTransactionManager();
		transctionManager.setEntityManagerFactory(entityManagerFactory);
		return transctionManager;
	}
	
	
}
