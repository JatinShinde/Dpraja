package com.spdpboss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf.disable()) 
	        .authorizeHttpRequests(auth -> auth
	            // 1. PUBLIC PAGES: Add "/" and "/index" here
	            .requestMatchers("/", "/index", "/login", "/css/**", "/js/**", "/images/**", "/uploads/**", "/robots.txt", "/sitemap.xml").permitAll()
	            
	            // 2. PROTECTED PAGES: Only logged in users can access /admin
	            .requestMatchers("/admin/**").authenticated()
	            
	            // 3. Fallback: Everything else is allowed
	            .anyRequest().permitAll()
	        )
	        .formLogin(form -> form
	            .loginPage("/login")
	            .defaultSuccessUrl("/admin", true)
	            .permitAll()
	        )
	        .logout(logout -> logout.permitAll());

	    return http.build();
	}

    @Bean
    public UserDetailsService userDetailsService() {
        // Username: admin | Password: admin123
        @SuppressWarnings("deprecation")
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("Raje@0909")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}