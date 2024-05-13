package com.example.finanzasbackend.dto.negocio

data class CrearNegocioResponse(
        val id:Long,
        val nombre:String,
        val ruc:String,
        val telefono:String,
        val direccion:String,
        val email:String,
)
