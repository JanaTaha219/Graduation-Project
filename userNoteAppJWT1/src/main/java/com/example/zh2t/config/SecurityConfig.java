package com.example.zh2t.config;

import com.example.zh2t.filter.JwtAuthenticationFilter;
import com.example.zh2t.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
         http.authorizeHttpRequests(
                        req ->  req
                                .requestMatchers("/api/v1/users/does/{userAId}/follow/{userBId}").permitAll()
                                .requestMatchers("/api/v1/users/followersCount/{userId}").permitAll()
                                .requestMatchers("/api/v1/users/followingCount/{userId}").permitAll()
                                .requestMatchers("/api/v1/notes/numberOfLikes/{noteId}").permitAll()
                                .requestMatchers("/api/v1/notes/numberOfSaving/{noteId}").permitAll()
                                .requestMatchers("/v3/**", "/swagger-ui/**", "/v3/api-docs", "http://localhost:8080/v3/api-docs").permitAll()
                                .requestMatchers("/api/v1/users/authenticate").permitAll()
                                .requestMatchers("/api/v1/notes/like/{noteId}").permitAll()
                                .requestMatchers("/api/v1/users", "POST").permitAll()
                                .requestMatchers("/api/v1/users/{id}/followers", "GET").permitAll()
                                .requestMatchers("/api/v1/users/{id}", "GET").permitAll()
                                .requestMatchers("/api/v1/users/{id}/following", "GET").permitAll()
                                .requestMatchers("/api/v1/users/{userId}/notes", "GET").permitAll()
                                .requestMatchers("/api/v1/users/currentUser", "GET").permitAll()
                                .requestMatchers("/api/v1/notes/{noteId}", "GET").permitAll()
                                .requestMatchers("/api/v1/users/forgetPassword", "POST").permitAll()
                                .requestMatchers("/api/v1/users/redis", "POST").permitAll()
                                .requestMatchers("/api/v1/users/redis/{key}", "GET").permitAll()
                                .anyRequest()
                                .permitAll()
                )
            .httpBasic(withDefaults())
            .formLogin(withDefaults())
                .userDetailsService(userDetailsService)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        //return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
