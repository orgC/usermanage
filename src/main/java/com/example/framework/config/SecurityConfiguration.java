package com.example.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfiguration {
	private static final String[] IGNORE_API = new String[]{"/","/home/index"};


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().authorizeHttpRequests(
				(authz) -> authz.antMatchers(IGNORE_API).permitAll()
						.anyRequest()
						.authenticated()
				).oauth2ResourceServer()
				.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
		return http.build();
	}

	private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
		JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
		jwtConverter.setJwtGrantedAuthoritiesConverter(new RealmRoleConverter());
		return jwtConverter;
	}

	/*
	 * @Bean public GrantedAuthorityDefaults grantedAuthorityDefaults() { return new
	 * GrantedAuthorityDefaults("SCOPE_"); }
	 */
}
