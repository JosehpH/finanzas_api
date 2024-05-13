package com.example.finanzasbackend.dto.producto

data class ProductoResponse(
        val id:Long,
        val nombre: String,
        val descripcion: String,
        val imagenes: List<String>,
        val precio: Float
)
