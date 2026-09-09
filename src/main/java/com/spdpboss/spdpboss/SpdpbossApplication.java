package com.spdpboss.spdpboss;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@EnableAsync        
@SpringBootApplication(scanBasePackages = {"com.spdpboss", "com.spdpboss.spdpboss"})
@EnableJpaRepositories(basePackages = "com.spdpboss.repository")
@EntityScan(basePackages = "com.spdpboss.model")
@EnableScheduling 
public class SpdpbossApplication {
	
	@PostConstruct
	public void init() {
	    TimeZone.setDefault(TimeZone.getTimeZone("IST"));
	}

    public static void main(String[] args) { 
        SpringApplication.run(SpdpbossApplication.class, args);
    }
}
