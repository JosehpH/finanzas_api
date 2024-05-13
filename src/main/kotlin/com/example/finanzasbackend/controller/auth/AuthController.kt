package com.example.finanzasbackend.controller.auth;

import com.example.finanzasbackend.dto.auth.AuthenticationRequest
import com.example.finanzasbackend.dto.auth.AuthenticationResponse
import com.example.finanzasbackend.service.auth.AuthenticationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Controller para la autenticación del negocio")
public class AuthController(
        private val authenticationService: AuthenticationService
) {
    @Operation(summary = "Ingresa las credenciales del negocio para obtener el Jwt que te permitirá hacer peticiones a los otros endpoints")
    @PostMapping
    fun authenticate(@RequestBody authRequest: AuthenticationRequest): AuthenticationResponse =
            authenticationService.authentication(authRequest)

}
