package com.example.finanzasbackend.dto.orden

import com.example.finanzasbackend.dto.producto.ProductoResponse

data class OrderItemResponse(
        val id:Long,
        val producto: ProductoResponse,
        val cantidad:Int,
        val subTotal:Float
)
