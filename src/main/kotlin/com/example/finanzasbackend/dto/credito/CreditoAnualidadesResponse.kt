package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.dto.gracia.GraciaResponse
import com.example.finanzasbackend.dto.tasa.TasaRequest
import java.time.LocalDate

class CreditoAnualidadesResponse(
        id:Long,
        tipoCredito:String,
        saldo: Float,
        pagoInicial: Float,
        saldoRestante: Float,
        tasaCompensatoria: TasaRequest,
        tasaMoratoria: TasaRequest,
        fechaDesembolso: LocalDate,
        val numCuotas:Int,
        val cuotas: List<CuotaResponse>,
        var periodoGracia:GraciaResponse?
): CreditoResponse(id,tipoCredito, saldo, pagoInicial, saldoRestante, tasaCompensatoria, tasaMoratoria, fechaDesembolso){

}