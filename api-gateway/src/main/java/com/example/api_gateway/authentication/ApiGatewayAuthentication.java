package com.example.api_gateway.authentication;
/* 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import static org.springframework.security.config.Customizer.withDefaults;

import com.example.dtos.UserDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity

public class ApiGatewayAuthentication {

	@Bean
	public MapReactiveUserDetailsService userDetailsService(BCryptPasswordEncoder encoder) {
		List<UserDetails> users = new ArrayList<>();
		
		List<UserDto> usersFromDatabase;
		usersFromDatabase = Arrays.asList(new RestTemplate().getForEntity("http://localhost:8081/user-service/users",UserDto[].class).getBody());
		
		for(UserDto c: usersFromDatabase) {
			users.add(User.withUsername(c.getEmail()).password(encoder.encode(c.getPassword())).roles(c.getRole()).build());
		}
		return new MapReactiveUserDetailsService(users);
	}
	
	@Bean
	public BCryptPasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Bean
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
		http.csrf(csrf -> csrf.disable())
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/user-service/**").permitAll()
            .pathMatchers("/course-service/**").permitAll()
            .pathMatchers("/payment-service/**").permitAll()
            .pathMatchers("/notification-service/**").permitAll()
            .pathMatchers("/review-service/**").permitAll()
        )
        .httpBasic(httpBasic -> httpBasic.disable()); 
		
		return http.build();
	}
	@Bean 
	public SecurityWebFilterChain filterChain(ServerHttpSecurity http){
		 http.csrf().disable().authorizeExchange(exchanges -> exchanges
			 .pathMatchers("/user-service/**").permitAll()
			 .pathMatchers("/course-service/**").permitAll()
			 .pathMatchers("/review-service/**").permitAll()
			 .pathMatchers("/payment-service/**").permitAll()
			 .pathMatchers("/notification-service/**").permitAll()
			 .anyExchange().permitAll())
			 .httpBasic(withDefaults());
		 return http.build();
	}
}
*/