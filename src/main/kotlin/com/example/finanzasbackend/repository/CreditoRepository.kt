package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.model.credito.Credito
import org.springframework.cglib.core.Local
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CreditoRepository :JpaRepository<Credito,Long>{
    fun countByCuenta_Cliente_Negocio(negocio:Negocio):Int
    fun findByCuenta_Cliente_Negocio_Email(email:String):List<Credito>

    fun findByCuenta_Cliente_Dni_AndAndCuenta_Cliente_Negocio_Email(dni:String,emailNegocio:String):List<Credito>

    fun findByCuenta_Cliente_Negocio_EmailAndCuenta_Cliente_DniAndFechaDesembolsoBetween(negocioEmail:String, dniCliente:String?, fechaInicio:LocalDate?, fechaFin:LocalDate?):List<Credito>

    fun findByCuenta_Cliente_Negocio_EmailAndFechaDesembolsoBetween(negocioEmail:String, fechaInicio:LocalDate, fechaFin:LocalDate):List<Credito>

}