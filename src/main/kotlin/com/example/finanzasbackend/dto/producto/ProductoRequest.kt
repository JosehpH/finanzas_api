package com.example.finanzasbackend.dto.producto

data class ProductoRequest(
        val nombre:String,
        val descripcion:String,
        val imagenes:List<String>,
        val precio:Float
)
