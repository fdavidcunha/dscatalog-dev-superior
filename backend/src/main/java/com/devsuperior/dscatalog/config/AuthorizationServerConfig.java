package com.devsuperior.dscatalog.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.devsuperior.dscatalog.components.JWTTokenEnhancer;

// @EnableAuthorizationServer -> Informa que essa é a classe que representa o AuthorizationServer do oauth.
// É a classe que recebe as credenciais e devolve um token na resposta.

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${security.oauth2.client.client-id}")
	private String clientId;
	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;
	@Value("${jwt.duration}")
	private Integer jwtDuration;

	@Autowired private BCryptPasswordEncoder   passwordEncoder;
	@Autowired private JwtAccessTokenConverter accessTokenConverter;
	@Autowired private JwtTokenStore           tokenStore;
	@Autowired private AuthenticationManager   authenticationManager;
	@Autowired private JWTTokenEnhancer        tokenEnhancer;  
	
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// Define como que vai ser a autenticação e quis serão os dados do cliente.

		clients.inMemory()                                // Define que o processo ocorrerá em memória.
			.withClient(clientId)                         // ID da aplicação. Serivá para se comunicar com o backend.
			.secret(passwordEncoder.encode(clientSecret)) // Senha da aplicação.
			.scopes("read", "write")                      // Define que a aplicação terá acesso de leitura e escrita.
			.authorizedGrantTypes("password")             // Tipo de acesso/login.
			.accessTokenValiditySeconds(jwtDuration);     // Tempo de expiração do token (em segundos).
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		
		// Adicionando no token informações extras sobre o usuário.
		TokenEnhancerChain chain = new TokenEnhancerChain();
		chain.setTokenEnhancers(Arrays.asList(accessTokenConverter, tokenEnhancer));
		
		endpoints.authenticationManager(authenticationManager) // Define quem vai autorizar e qual será o formato do token.
			.tokenStore(tokenStore)                            // Define quais serão os objetos responsáveis por processar o token.
			.accessTokenConverter(accessTokenConverter)
			.tokenEnhancer(chain);
	}
}