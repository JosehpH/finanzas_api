package com.example.finanzasbackend.dto.gracia

data class GraciaResponse(
        val id:Long,
        val numCuotas:Long,
        val tipo:String,
        val saldoRestante:Float
)
