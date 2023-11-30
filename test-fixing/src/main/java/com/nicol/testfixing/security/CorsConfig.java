package com.nicol.testfixing.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

public class CorsConfig  implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Configura CORS per tutti gli endpoint
                .allowedOrigins("http://localhost:5173") // Consenti solo da questo dominio
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Metodi consentiti
                .allowedHeaders("*"); // Tutti gli headers consentiti
    }
}