package br.com.lata.velha.authentication.infrastructure.security.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(auth -> auth
                        // público
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        // CRUD — USER e ADMIN
                        .requestMatchers("/proprietarios/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/veiculos/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/funcionarios/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/ordens-servico/create").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/aprovar").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/*/reprovar").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/*/iniciar").hasAnyRole("MECANICO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/adiciona-servico").hasAnyRole("MECANICO", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/ordens-servico/*/*/finalizar-diagnostico").hasAnyRole("MECANICO", "ADMIN")
                        .requestMatchers("/ordens-servico/**").hasAnyRole("USER", "MECANICO", "ADMIN")


                        // qualquer outra rota exige autenticação
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Token inválido ou expirado\"}");
                        })
                );

        return http.build();
    }
}