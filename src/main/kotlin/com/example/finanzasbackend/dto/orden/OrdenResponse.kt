package com.example.finanzasbackend.dto.orden

data class OrdenResponse(
        val id:Long,
        val items:List<OrderItemResponse>,
        val total: Float
)
