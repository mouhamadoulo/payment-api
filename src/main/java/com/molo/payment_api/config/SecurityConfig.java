package com.molo.payment_api.config;

import com.molo.payment_api.security.CustomUserDetailsService;
import com.molo.payment_api.security.JwtRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Comprendre les éléments essentiels :
 *
 * | Élément                        | Rôle                                         |
 * | ------------------------------ | -------------------------------------------- |
 * | `SecurityFilterChain`          | Définit les règles de sécurité HTTP          |
 * | `PasswordEncoder`              | Hash les mots de passe                       |
 * | `BCryptPasswordEncoder`        | Algorithme de hash des mots de passe         |
 * | `AuthenticationManager`        | Coordonne l'authentification                 |
 * | `DaoAuthenticationProvider`    | Vérifie les identifiants à partir de la base |
 * | `STATELESS`                    | Ne conserve pas de session HTTP              |
 * | `permitAll()`                  | Autorise l'accès sans authentification       |
 * | `authenticated()`              | Exige un utilisateur authentifié             |
 * | `hasRole("ADMIN")`             | Exige le rôle ADMIN                          |
 * | `oauth2ResourceServer().jwt()` | Active la validation des JWT                 |
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationProvider authenticationProvider) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider)

                .authorizeHttpRequests(auth -> auth

                        // Endpoints publics
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Administration
                        .requestMatchers("/api/v1/admin/**")
                        .hasRole("ADMIN")

                        // Tous les autres endpoints nécessitent un JWT
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new JwtRoleConverter())
                        )
                );

        return http.build();
    }
}
