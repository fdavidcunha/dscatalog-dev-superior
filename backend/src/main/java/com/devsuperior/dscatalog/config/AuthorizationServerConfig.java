package com.devsuperior.dscatalog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

// @EnableAuthorizationServer -> Informa que essa é a classe representa o AuthorizationServer do oauth.

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired private BCryptPasswordEncoder   passwordEncoder;
	@Autowired private JwtAccessTokenConverter accessTokenConverter;
	@Autowired private JwtTokenStore           tokenStore;
	@Autowired private AuthenticationManager   authenticationManager;
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Define como que vai ser a autenticação e quis serão os dados do cliente.

		clients.inMemory()                                  // Define que o processo ocorrerá em memória.
			.withClient("dscatalog")                        // ID da aplicação. Serivá para se comunicar com o backend.
			.secret(passwordEncoder.encode("dscatalog123")) // Senha da aplicação.
			.scopes("read", "write")                        // Define que a aplicação terá acesso de leitura e escrita.
			.authorizedGrantTypes("password")               // Tipo de acesso/login.
			.accessTokenValiditySeconds(86400);             // Tempo de expiração do token (em segundos).
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		endpoints.authenticationManager(authenticationManager) // Define quem vai autorizar e qual será o formato do token.
			.tokenStore(tokenStore)                            // Define quais serão os objetos responsáveis por processar o token.
			.accessTokenConverter(accessTokenConverter);
	}
}