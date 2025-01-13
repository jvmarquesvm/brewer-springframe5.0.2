package com.algaworks.brewer.config;

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.algaworks.brewer.security.AppUserDetailsService;

//@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses =  AppUserDetailsService.class) // Configuração para encontrar o  userDetailsService
@EnableGlobalMethodSecurity(prePostEnabled = true) // anotação para habilitar a anotação @PreAuthorize no metodo cancelar
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO Auto-generated method stub
		//super.configure(auth);
		
		//auth.inMemoryAuthentication().withUser("admin").password("admin").roles("CADASTRO_CLIENTE");
		
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEconder());
	}
	
	//pode ser configurado as excessões desta forma
	@Override
	public void configure(WebSecurity web) throws Exception {
		// TODO Auto-generated method stub
		//super.configure(web);
		web.ignoring().antMatchers("/layout/**")
		               .antMatchers(HttpMethod.PUT, "/usuario/status");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		//Configurar as restrições primeiros e as liberações por ultimo
		//super.configure(http);
		http.authorizeRequests().antMatchers("/cidade/novo")//.hasAnyRole([])
											    //utilizar o hasRole caso no nome tem o prefix ROLE_
		                                                    //.hasRole("CADASTRAR_CIDADE")
		                                                    .hasAuthority("CADASTRAR_CIDADE")
		                                                    //para configurar as permissoes deverá observar se foi config. por Role ou Authority
		                        .antMatchers("/usuario/**")
		                                .hasAuthority("CADASTRAR_USUARIO")
		                                        //se não, utilizar o hasAuthority
								//.antMatchers("/layout/**").permitAll()
								//.anyRequest().denyAll()
		                        .anyRequest().authenticated()
			.and()
			.formLogin().loginPage("/login").permitAll() //Nao precisa estar autenticado
			.and()
			.logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // Configuração para permitir acessa a página de logout após habilitar o csrf
			.and()
			.exceptionHandling().accessDeniedPage("/403")
			//.and()
			//.csrf().disable(); // sem essa configuração não consegue submeter para formulários que não tenham o action configurado e form para o método post
			.and()
			.sessionManagement().maximumSessions(1).expiredUrl("/login")  //Configuração para redirecionar para pagina login quando houver mais de 1 sessão aberta/ se não tiver página configurada.. vai ter uma mensagem
			                          .and().invalidSessionUrl("/login"); // Configuração para redirecionar para a página de login quando houver uma sessão expirada por falta de uso do usuário
	}
	
	@Bean
	public PasswordEncoder passwordEconder() {
		return new BCryptPasswordEncoder();
	}
}
