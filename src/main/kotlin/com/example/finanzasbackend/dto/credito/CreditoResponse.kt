package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.tasa.TasaRequest
import java.time.LocalDate

abstract  class CreditoResponse(
        val id:Long,
        val tipoCredito:String,
        val saldo:Float,
        val pagoInicial:Float,
        val saldoRestante:Float,
        val tasaCompensatoria: TasaRequest,
        val tasaMoratoria: TasaRequest,
        val fechaDesembolso: LocalDate,
){

}
