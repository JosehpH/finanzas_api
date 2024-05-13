package com.example.finanzasbackend.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
        val key: String,
        val accessTokenExpiration: Long,
        val refreshTokenExpiration: Long
)
