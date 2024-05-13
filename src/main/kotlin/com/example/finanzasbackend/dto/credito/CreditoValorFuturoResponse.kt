package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.dto.tasa.TasaRequest
import java.time.LocalDate

class CreditoValorFuturoResponse(
        id:Long,
        tipoCredito:String,
        saldo: Float,
        pagoInicial: Float,
        saldoRestante: Float,
        tasaCompensatoria: TasaRequest,
        tasaMoratoria: TasaRequest,
        fechaDesembolso: LocalDate,
        val cuota: CuotaResponse
) : CreditoResponse(id,tipoCredito,saldo, pagoInicial, saldoRestante, tasaCompensatoria, tasaMoratoria, fechaDesembolso) {

}
