package com.sia.salesapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // <--- 1. ACTIVĂM CORS AICI
                .csrf(csrf -> csrf.disable())    // 2. Dezactivăm CSRF pentru API
                .authorizeHttpRequests(auth -> auth
                        // 3. Permitem accesul public la Swagger
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 4. Permitem accesul public la Înregistrare și Login (CRITIC!)
                        .requestMatchers("/api/users/**").permitAll()
                        // Orice altceva cere autentificare
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 5. Configurare explicită pentru a accepta cereri de la React (localhost:5173)
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Se aplică pe toate rutele
                        .allowedOrigins("http://localhost:5173") // <--- Adresa Frontend-ului tău
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}