package com.kodlamaio.commonpackage.configuration.security;

import com.kodlamaio.commonpackage.utils.security.KeycloakJwtRoleConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakJwtRoleConverter());

        http.cors().and().authorizeHttpRequests()

                // #1
                .requestMatchers(
                        // herkes erişsin
                        "/api/filters",

                        // clients
                        /// car-client
                        "/api/cars/check-car-available/**",
                        "/api/cars/daily-price/**",
                        "/api/cars/details/**",

                        /// payment-client
                        "/api/payments/process-payment",
                        "/api/payments/card-holder/**"
                )
                .permitAll()

                // #2
                .requestMatchers("/api/**")
                .hasAnyRole("user")
                .anyRequest()
                .authenticated()

                // #csrf
                .and()
                .csrf().disable()

                // #identical server
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(converter);

        return http.build();
    }
}
