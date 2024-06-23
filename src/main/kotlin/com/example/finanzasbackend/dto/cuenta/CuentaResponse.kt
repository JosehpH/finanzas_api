package com.example.finanzasbackend.dto.cuenta

import com.example.finanzasbackend.dto.credito.CreditoResponse
import com.example.finanzasbackend.dto.tasa.TasaRequest
import java.time.LocalDate

data class CuentaResponse(
        val id:Long,
        val limiteCrediticio:Float,
        val creditos:List<CreditoResponse?>?=null,
        val interesAcumulado:Float?=null
)

