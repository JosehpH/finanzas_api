package com.example.finanzasbackend.config.jwt

import com.example.finanzasbackend.repository.NegocioRepository
import com.example.finanzasbackend.service.auth.CustomUserDetailsService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class Configuration {

    @Bean
    fun userDetailsService(negocioRepository: NegocioRepository): UserDetailsService =
            CustomUserDetailsService(negocioRepository)
    @Bean
    fun enconder():PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(negocioRepository: NegocioRepository): AuthenticationProvider =
            DaoAuthenticationProvider().also {
                it.setPasswordEncoder(enconder())
                it.setUserDetailsService(userDetailsService(negocioRepository))
            }
    @Bean
    fun authenticationManager(config:AuthenticationConfiguration):AuthenticationManager  =
            config.authenticationManager
}