package com.example.payment_service.security;

/* 
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;



@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
   /* 
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated())
            .oauth2Login(withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
            .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable() 
            .authorizeRequests()
                .anyRequest().permitAll()  
            .and()
            .httpBasic().disable() 
            .formLogin().disable()
            .build();
    }
}*/
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

   /*  @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/payment-service/**").hasAuthority("SCOPE_internal")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());

        return http.build();
    }
        */

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/payment-service/**").hasAuthority("SCOPE_internal")
                            .anyRequest().authenticated())  
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt())  
                    .exceptionHandling(e -> e
                            .authenticationEntryPoint((request, response, authException) -> {
                               
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                            }))
                    .formLogin(login -> login.disable()) 
                    .httpBasic(basic -> basic.disable()) 
                    .oauth2Login(login -> login.disable());  
    
            return http.build();
        }


}

