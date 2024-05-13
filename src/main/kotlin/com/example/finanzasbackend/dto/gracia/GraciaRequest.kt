package com.example.finanzasbackend.dto.gracia

import com.example.finanzasbackend.model.credito.gracia.TipoPeriodoGracia

data class GraciaRequest(
        val numCuotas:Int,
        val tipo:String = TipoPeriodoGracia.TOTAL.name
)