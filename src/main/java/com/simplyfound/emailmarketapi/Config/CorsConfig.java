package com.simplyfound.emailmarketapi.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOrigin("https://email.simplyfound.com.na");
        config.addAllowedOrigin("https://emailmarketin.simplyfound.com.na");
        config.addAllowedOrigin("https://www.simplyfound.com.na");
        config.addAllowedOrigin("https://simplyfound.com.na");
        config.addAllowedOrigin("http://localhost:3007");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}


