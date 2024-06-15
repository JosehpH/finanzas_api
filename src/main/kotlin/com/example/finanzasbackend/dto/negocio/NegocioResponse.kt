package com.example.finanzasbackend.dto.negocio

data class NegocioResponse(
        val id: Long,
        val nombre: String,
        val ruc: String,
        val telefono: String,
        val direccion: String,
        val email: String,
        val clientesActivos:Int,
        val creditosOtorgados:Int,
        val creditosPendientesPago:Int,
        val creditosPagoAtrasado:Int
)
