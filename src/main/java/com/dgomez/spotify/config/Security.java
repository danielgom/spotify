package com.dgomez.spotify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@EnableWebSecurity
@Configuration
public class Security {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsRegistration = new CorsConfiguration();
                    corsRegistration.setAllowedOrigins(List.of("*"));
                    corsRegistration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    corsRegistration.setAllowCredentials(true);
                    return corsRegistration;
                }))
                .authorizeHttpRequests(requests ->
                        requests.requestMatchers("/login", "/oauth2/**").permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(oAuth2 -> oAuth2
                        .defaultSuccessUrl("/auth/spotify", true)
                        .failureUrl("/login?error"))
                .build();
    }
}
