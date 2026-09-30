package com.spdpboss.config;

import com.spdpboss.model.TodayFinal;
import com.spdpboss.repository.TodayFinalRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserDetailsService userDetailsService(TodayFinalRepository todayFinalRepository) {
        return username -> {
            if ("admin".equalsIgnoreCase(username)) {
                String pwd = todayFinalRepository.findById(17L)
                        .map(TodayFinal::getContent)
                        .filter(c -> c != null && !c.trim().isEmpty())
                        .orElse("Raje@0909");

                @SuppressWarnings("deprecation")
                UserDetails user = User.withDefaultPasswordEncoder()
                        .username("admin")
                        .password(pwd)
                        .roles("ADMIN")
                        .build();
                return user;
            }
            throw new UsernameNotFoundException("User not found: " + username);
        };
    }
}