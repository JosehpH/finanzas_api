package com.example.finanzasbackend.dto.tasa

data class TasaRequest(
        val periodo:String,
        val tasa:Float,
        val tipo: String,
        val periodoCapitalizacion:String? =null
)