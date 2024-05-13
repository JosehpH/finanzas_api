package com.example.finanzasbackend.controller.cuota

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.model.credito.Cuota
import com.example.finanzasbackend.service.CuotaService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cuota")
@Tag(name = "Cuota", description = "Controlador para las cuotas")
class CuotaController(
        private val cuotaService: CuotaService
) {
    @Operation(summary = "En esta función puedes pagar cualquier cuota y actualizar el limite crediticio recuperando la amortización")
    @PostMapping("/{cuentaId}/{cuotaId}/pagar")
    fun pagarCuota(@PathVariable cuentaId:Long,@PathVariable cuotaId:Long, @RequestParam metodoPago:String):ResponseEntity<CuotaResponse>{
        val cuota = cuotaService.pagarCuota(cuentaId = cuentaId, cuotaId = cuotaId, metodoPago = metodoPago)
        return ResponseEntity(cuota.toResponse(),HttpStatus.OK)
    }
    private fun Cuota.toResponse():CuotaResponse =
            CuotaResponse(
                    id = this.id,
                    fechaVencimiento = this.fechaVencimiento,
                    amortizacion = this.amortizacion,
                    interesCompensatorio = this.interesCompensatorio,
                    monto = this.monto,
                    numeroCuota = this.numeroDeCuota,
                    fechaPago = this.fechaPago,
                    metodoPago = this.metodoPago,
                    estadoCuota = this.estadoCuota?.name,
                    interesMoratorio = this.interesMoratorio,
                    interesCompensatorioMora = this.interesCompensatorioMora
            )
}