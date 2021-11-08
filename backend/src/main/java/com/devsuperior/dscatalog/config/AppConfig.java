package com.devsuperior.dscatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Classe de configuração responsável por manter alguma configuração ou criar algum componente.

@Configuration
public class AppConfig {

	// Um Bean é um componente do Spring, assim como o @Service e etc.
	// O @Bean é uma anotation de método e não de classe, como o @Service.
	// Essa anotation define que o método será um componente gerenciado pelo SpringBoot.
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}