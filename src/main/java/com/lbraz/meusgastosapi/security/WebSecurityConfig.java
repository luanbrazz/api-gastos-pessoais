package com.lbraz.meusgastosapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private UserDetailsSecurityServer userDetailsSecurityServer;

    // Configura um bean para o BCryptPasswordEncoder
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura um bean para o AuthenticationManager usando o authenticationConfiguration injetado
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Configura um bean para o SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().disable().and() // Desabilita o header "frameOptions"
                .cors().and() // Habilita o suporte a CORS
                .csrf().disable() // Desabilita a proteção CSRF
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Permite todas as requisições POST para "/meusgastos/cadastrar-usuario"
                        .anyRequest().authenticated()) // Exige autenticação para qualquer outra requisição
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Configura a política de criação de sessão como STATELESS (sem estado)

        // Adiciona os filtros personalizados JwtAuthenticationFilter e JwtAuthorizationFilter ao HttpSecurity
        http.addFilter(new JwtAuthenticationFilter(authenticationManager(authenticationConfiguration), jwtUtil));
        http.addFilter(new JwtAuthorizationFilter(authenticationManager(authenticationConfiguration), jwtUtil, userDetailsSecurityServer));

        return http.build();
    }
}
