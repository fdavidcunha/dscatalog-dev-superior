package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

// Classe de configuração responsável por manter alguma configuração ou criar algum componente.

@Configuration
public class AppConfig {

	@Value("${jwt.secret}")
	private String jwtSecret;
	
	// Um Bean é um componente do Spring, assim como o @Service e etc.
	// O @Bean é uma anotation de método e não de classe, como o @Service.
	// Essa anotation define que o método será um componente gerenciado pelo SpringBoot.
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		// Objeto capaz de acessar/ler/decodificar um token JWT.
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(jwtSecret);
		return tokenConverter;
	}

	@Bean
	public JwtTokenStore tokenStore() {
		// Bean para acessar o token.
		return new JwtTokenStore(accessTokenConverter());
	}	
}