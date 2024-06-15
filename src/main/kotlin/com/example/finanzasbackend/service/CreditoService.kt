package com.example.finanzasbackend.service;

import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.EstadoCuota
import com.example.finanzasbackend.repository.CreditoRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CreditoService(
        private val creditoRepository: CreditoRepository
) {
    fun calcularCuotasValorFuturo(credito: CreditoValoFuturo):CreditoValoFuturo{
        credito.calcularCuotas()
        return credito
    }
    fun calcularCuotasAnualidad(credito: CreditoAnualidad):CreditoAnualidad{
        credito.calcularCuotas()
        return credito
    }
    fun getAllCreditos():List<Credito>{
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        return creditoRepository.findByCuenta_Cliente_Negocio_Email(negocioEmail)
    }
    fun getByDni(dni:String):List<Credito>{
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        return creditoRepository.findByCuenta_Cliente_Dni_AndAndCuenta_Cliente_Negocio_Email(dni,negocioEmail)
    }
    fun getById(id:Long):Credito{
        return creditoRepository.findById(id).get()
    }
    fun filter(dni: String?, fechaInicio: LocalDate?, fechaFin: LocalDate?): List<Credito> {
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        println("DNI: $dni, Fecha Inicio: $fechaInicio, Fecha Fin: $fechaFin")

        // Si todos los parámetros son nulos, devolver todos los créditos
        if (dni == null && fechaInicio == null && fechaFin == null) {
            return this.getAllCreditos()
        } else if (dni != null && fechaInicio == null && fechaFin == null) {
            // Si sólo el DNI está presente, filtrar por DNI
            return this.getByDni(dni)
        } else if (dni != null && fechaInicio != null && fechaFin != null) {
            // Si tenemos DNI y fechas, filtrar por DNI y fechas
            return creditoRepository.findByCuenta_Cliente_Negocio_EmailAndCuenta_Cliente_DniAndFechaDesembolsoBetween(
                    negocioEmail,
                    dni,
                    fechaInicio,
                    fechaFin
            )
        } else {
            // En otros casos, utilizar la consulta con fechas
            return creditoRepository.findByCuenta_Cliente_Negocio_EmailAndFechaDesembolsoBetween(
                    negocioEmail,
                    fechaInicio ?: LocalDate.MIN, // Utilizar la fecha mínima si es nula
                    fechaFin ?: LocalDate.MAX // Utilizar la fecha máxima si es nula
            )
        }
    }

    fun getCreditosByEstadoCuota(estadoCuota:EstadoCuota):List<Credito>{
        val creditos = getAllCreditos()
        return creditos.filter { credito ->
            when (credito) {
                is CreditoAnualidad -> {
                    credito.cuotas.any { it.estadoCuota == estadoCuota }
                }
                is CreditoValoFuturo -> {
                    credito.cuota!!.estadoCuota == estadoCuota
                }
                else -> false
            }
        }
    }
}
