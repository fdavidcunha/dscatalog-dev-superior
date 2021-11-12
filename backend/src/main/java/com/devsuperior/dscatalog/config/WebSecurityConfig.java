package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired private BCryptPasswordEncoder passwordEncoder;
	@Autowired private UserDetailsService userDetailsService;
	
 	@Override
	public void configure(WebSecurity web) throws Exception {
		// web.ignoring().antMatchers("/**")          -> Libera todos os caminhos da aplicação.
		// web.ignoring().antMatchers("/actuator/**") -> Libera todos os caminhos da aplicação passando pelo "actuator" que é uma biblioteca que o Spring Cloud oauth usa.

		web.ignoring().antMatchers("/**");
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Configurando qual é o algoritimo usado para criptografar senhas e quem é o UserDetailService.
		// Com essa configuração o spring security vai saber como ele vai buscar o usuário (por email)
		// e como ele vai analisar a senha criptografada (passwordEncoder).
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder); 
	}

	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}
}