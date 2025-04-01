package com.example.api_gateway.security;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;


@Configuration
@EnableWebFluxSecurity


public class OktaOAuth2WebSecurity {

     @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
         http
         .csrf(csrf -> csrf.disable()) 
                 .authorizeExchange(exchange -> exchange
                 .pathMatchers(HttpMethod.POST, "/user-service/users/create").permitAll() 
                                 .pathMatchers("/authenticate/login").permitAll()
                                 .anyExchange().authenticated() // Ensure all exchanges are authenticated
                 )
                 .oauth2ResourceServer(resourceServer -> resourceServer
                                 .jwt(Customizer.withDefaults()) // Use JWT for OAuth2 resource server
                 )
                 .exceptionHandling(handling -> handling
                         .authenticationEntryPoint((exchange, exception) -> {
                             
                             exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                             return exchange.getResponse().setComplete(); 
                         }));

        return http.build();
    }
}

