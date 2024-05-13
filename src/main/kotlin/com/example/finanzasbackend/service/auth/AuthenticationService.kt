package com.example.finanzasbackend.service.auth

import com.example.finanzasbackend.config.jwt.JwtProperties
import com.example.finanzasbackend.dto.auth.AuthenticationRequest
import com.example.finanzasbackend.dto.auth.AuthenticationResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
        private val authenticationManager: AuthenticationManager,
        private val userDetailsService: CustomUserDetailsService,
        private val tokenService: TokenService,
        private val jwtProperties: JwtProperties
) {
    fun authentication(authRequest: AuthenticationRequest): AuthenticationResponse {
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        authRequest.email,
                        authRequest.password
                )
        )
        val user = userDetailsService.loadUserByUsername(authRequest.email)
        val accessToken = tokenService.generate(
                userDetails = user,
                expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)
        )
        return AuthenticationResponse(accessToken = accessToken)
    }
}