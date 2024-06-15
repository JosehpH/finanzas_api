package com.example.finanzasbackend.controller.negocio;

import com.example.finanzasbackend.dto.negocio.ActualizarNegocioRequest
import com.example.finanzasbackend.dto.negocio.CrearNegocioRequest
import com.example.finanzasbackend.dto.negocio.CrearNegocioResponse
import com.example.finanzasbackend.dto.negocio.NegocioResponse
import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.service.NegocioService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
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

    @Operation(summary = "Obten los detalles generales de la cuenta del negocio")
    @GetMapping
    fun getNegocio():ResponseEntity<NegocioResponse>{
        val negocio: Negocio = negocioService.getNegocio() ?: throw BadRequestException("Negocio no encontrado")
        return ResponseEntity(negocio.toResponseHome(),HttpStatus.OK)
    }

    @Operation(summary="Actualiza los datos de la cuenta del negocio")
    @PutMapping
    fun updateNegocio(@RequestBody request:ActualizarNegocioRequest):ResponseEntity<CrearNegocioResponse>{
        val negocio:Negocio? = negocioService.updateNegocio(request.toModel());
        return ResponseEntity(negocio?.toResponse(),HttpStatus.OK)
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
    private fun Negocio.toResponseHome():NegocioResponse{
        return NegocioResponse(
                id = this.id,
                nombre=this.nombre,
                ruc=this.ruc,
                direccion=this.direccion,
                email = this.email,
                telefono = this.telefono,
                clientesActivos = this.getNumeroClientesActivos(),
                creditosOtorgados = this.getTotalCreditoOtorgados(),
                creditosPagoAtrasado = this.getNumeroCreditosPagoAtrasado(),
                creditosPendientesPago = this.getNumeroCreditosPagoPendiente()
        )
    }
    private fun ActualizarNegocioRequest.toModel():Negocio = Negocio(
            nombre = this.nombre,
            ruc = this.ruc,
            telefono = this.telefono,
            email = this.email,
            direccion = this.direccion,
            password = ""
    )
}
