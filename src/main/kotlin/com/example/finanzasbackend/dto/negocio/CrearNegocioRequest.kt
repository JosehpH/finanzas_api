package com.example.finanzasbackend.dto.negocio

data class CrearNegocioRequest(
        val nombre:String,
        val ruc:String,
        val telefono:String,
        val direccion:String,
        val email:String,
        val password:String
)
