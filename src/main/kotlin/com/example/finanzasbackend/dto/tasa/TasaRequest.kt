package com.example.finanzasbackend.dto.tasa

import com.example.finanzasbackend.model.credito.tasaInteres.TipoPeriodo

data class TasaRequest(
        val periodo:String = TipoPeriodo.MENSUAL.name,
        val tasa:Float,
        val tipo: String,
        val periodoCapitalizacion:String? =null
)