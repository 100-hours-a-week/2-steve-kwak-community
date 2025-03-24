package com.example.demo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/resources/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/js/**").permitAll()
                        .requestMatchers("/users/login").permitAll()
                        .requestMatchers("/users").permitAll()
                        .requestMatchers("/posts").permitAll()
                        .requestMatchers("/posts/upload/image/**").permitAll()
                        .requestMatchers("/path/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/postedit/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/*/password").permitAll()
                        .requestMatchers(HttpMethod.GET,"/header").permitAll()
                        .requestMatchers(HttpMethod.GET,"/header1").permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/api1/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/*/comments").permitAll()
                        .requestMatchers(HttpMethod.GET,"/posts/makepost").permitAll()
                        .requestMatchers(HttpMethod.GET,"/users/*/profile").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
