package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.clientes.ClienteResponse
import java.time.LocalDate

data class CreditoWithClienteResponse(
        val creditoId:Long,
        val tipoCredito:String,
        val saldo: Float,
        val pagoInicial: Float,
        val saldoRestante: Float,
        val cliente:ClienteResponse,
        val fechaDesembolso:LocalDate,
        val estadoCredito:String,
        val consumoId:Long
)
