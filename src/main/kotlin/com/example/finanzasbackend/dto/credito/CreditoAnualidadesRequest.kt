package com.example.finanzasbackend.dto.credito

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.dto.gracia.GraciaRequest
import com.example.finanzasbackend.dto.tasa.TasaRequest
import com.example.finanzasbackend.model.credito.tasaInteres.TipoPeriodo
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.SchemaProperty
import java.lang.annotation.ElementType
import java.time.LocalDate

data class CreditoAnualidadesRequest(
        val ordenId:Long,
        val pagoInicial: Float,
        val tasaCompensatoria: TasaRequest,
        val tasaMoratoria: TasaRequest,
        val fechaDesembolso: LocalDate = LocalDate.now(),
        val numCuotas:Int,
        val tipoAnualidad:String,
        val periodoPago:String = TipoPeriodo.MENSUAL.name,
        val gracia:GraciaRequest?
)
