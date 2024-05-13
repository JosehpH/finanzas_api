package com.example.finanzasbackend.controller.negocio;

import com.example.finanzasbackend.dto.negocio.CrearNegocioRequest
import com.example.finanzasbackend.dto.negocio.CrearNegocioResponse
import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.service.NegocioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/negocio")
@Tag(name = "Negocio", description = "Conjunto de endpoints para el CRUD del negocio")
class NegocioController(
        private val negocioService: NegocioService,
        private val passwordEncoder: PasswordEncoder
) {
    @Operation(summary = "Registra el negocio desde este endpoint")
    @PostMapping
    fun registrarNegocio(@RequestBody request: CrearNegocioRequest):ResponseEntity<CrearNegocioResponse>{
        val response:Negocio? =  negocioService.registrarNegocio(request.toModel())
        return ResponseEntity(response?.toResponse(),HttpStatus.CREATED)
    }

    private fun CrearNegocioRequest.toModel(): Negocio =
            Negocio(
                    nombre=this.nombre,
                    ruc = this.ruc,
                    telefono=this.telefono,
                    direccion = this.direccion,
                    email = this.email,
                    password = passwordEncoder.encode(this.password)
            )
    private fun Negocio.toResponse(): CrearNegocioResponse =
            CrearNegocioResponse(
                    id = this.id,
                    nombre=this.nombre,
                    ruc=this.ruc,
                    direccion=this.direccion,
                    email = this.email,
                    telefono = this.telefono
            )
}
