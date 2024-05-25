package com.example.finanzasbackend.dto.clientes

import com.example.finanzasbackend.dto.cuenta.CuentaResponse

data class ClienteResponse(
        val id:Long,
        val nombres:String,
        val apellidoPaterno:String,
        val apellidoMaterno:String,
        val dni:String,
        val email:String,
        val telefono:String,
        val photo:String?,
        val cuenta:CuentaResponse?
)
