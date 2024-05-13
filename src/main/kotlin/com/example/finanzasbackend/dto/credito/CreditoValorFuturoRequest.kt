package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.tasa.TasaRequest
import java.time.LocalDate

data class CreditoValorFuturoRequest(
        val ordenId:Long,
        val tasaCompensatoria:TasaRequest,
        val tasaMoratoria:TasaRequest,
        val pagoInicial:Float,
        val fechaDesembolso:LocalDate,
        val fechaVencimiento:LocalDate

)

