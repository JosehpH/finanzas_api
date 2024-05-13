package com.example.finanzasbackend.dto.cuota

import java.time.LocalDate


data class CuotaResponse(
        val id:Long,
        val numeroCuota:Int,
        val fechaVencimiento: LocalDate,
        val amortizacion: Float,
        val interesCompensatorio: Float,
        val interesMoratorio:Float,
        val interesCompensatorioMora:Float,
        val monto: Float,
        val fechaPago:LocalDate?,
        val metodoPago:String,
        val estadoCuota:String?
)

