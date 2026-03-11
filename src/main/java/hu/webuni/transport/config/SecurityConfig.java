package hu.webuni.transport.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import hu.webuni.transport.security.JwtAuthFilter;

@Configuration
public class SecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
		
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		
		http.authorizeHttpRequests(auth -> auth
				.requestMatchers(HttpMethod.POST, "/api/login").permitAll()
				
				//Address create/update csak ADDRESS_MANAGER
				.requestMatchers(HttpMethod.POST, "/api/addresses").hasAuthority("ADDRESS_MANAGER")
				.requestMatchers(HttpMethod.PUT, "/api/addresses/**").hasAuthority("ADDRESS_MANAGER")
				
				//Delay csak TRANSPORT_MANAGER
				.requestMatchers(HttpMethod.POST, "/api/transportplans/*/delay").hasAuthority("TRANSPORT_MANAGER")
				
				//permit h2-console:
				.requestMatchers("/h2-console/**").permitAll()
				
				
				.anyRequest().permitAll()
			);
		
			http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
			//permit h2-console:
			http.headers(headers -> headers.frameOptions(f -> f.sameOrigin()));
			
			return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService userDetailsService(PasswordEncoder encoder) {
		UserDetails addressMgr = User.withUsername("addressmgr")
				.password(encoder.encode("pass123"))
				.authorities("ADDRESS_MANAGER")
				.build();
		
		UserDetails transportMgr = User.withUsername("transportmgr")
				.password(encoder.encode("pass123"))
				.authorities("TRANSPORT_MANAGER")
				.build();
		
		UserDetails admin = User.withUsername("admin")
				.password(encoder.encode("pass123"))
				.authorities("ADDRESS_MANAGER", "TRANSPORT_MANAGER")
				.build();
		
		return new InMemoryUserDetailsManager(addressMgr, transportMgr, admin);
		
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		
		return config.getAuthenticationManager();
		
	}
	
	
	

}
