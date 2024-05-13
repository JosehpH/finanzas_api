package com.example.finanzasbackend.service.auth

import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.repository.NegocioRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CustomUserDetailsService(private val negocioRepository: NegocioRepository): UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails =
            negocioRepository.findByEmail(email).getOrNull()?.mapToUserDetails()
                    ?:throw UsernameNotFoundException("Negocio con email:$email no encontrado")

    private fun Negocio.mapToUserDetails():UserDetails =
            User.builder()
                    .username(this.email)
                    .password(this.password)
                    .roles(this.role.name)
                    .build()
}