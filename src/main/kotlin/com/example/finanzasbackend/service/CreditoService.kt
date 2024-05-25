package com.example.finanzasbackend.service;

import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import org.springframework.stereotype.Service

@Service
class CreditoService() {
    fun calcularCuotasValorFuturo(credito: CreditoValoFuturo):CreditoValoFuturo{
        credito.calcularCuotas()
        return credito
    }
    fun calcularCuotasAnualidad(credito: CreditoAnualidad):CreditoAnualidad{
        credito.calcularCuotas()
        return credito
    }
}
