package com.example.finanzasbackend.config.security

import com.example.finanzasbackend.config.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
        private val authenticationProvider: AuthenticationProvider
) {

    @Bean
    fun securityFilterChain(
            http: HttpSecurity,
            jwtAuthenticationFilter: JwtAuthenticationFilter,

            ): DefaultSecurityFilterChain =
            http
                    .csrf { it.disable() }
                    .cors {
                        it
                                .configurationSource {
                                    var cors = CorsConfiguration();
                                    cors.allowedOrigins = listOf("*");
                                    cors.allowedMethods = listOf("*");
                                    cors.allowedHeaders = listOf("*");
                                    cors.allowCredentials = true;
                                    cors.maxAge = 3600L;
                                    return@configurationSource cors;
                                }
                    }
                    .authorizeHttpRequests {
                        it
                                .requestMatchers("/api/auth", "api/auth/refrsh", "/error")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/user")
                                .permitAll()
                                .requestMatchers("/api/user**").hasRole("ADMIN")
                                .anyRequest()
                                .permitAll()

                    }
                    .sessionManagement {
                        it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    }
                    .authenticationProvider(authenticationProvider)
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
                    .build()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = mutableListOf("*")
        configuration.allowedMethods = mutableListOf("*")
        configuration.allowedHeaders = mutableListOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}