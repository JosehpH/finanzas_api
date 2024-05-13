package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.Orden
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface OrdenRepository:JpaRepository<Orden,Long> {
    fun findByCredito_Cuenta_Cliente_Id(clienteId:Long): List<Orden>
    fun findByCredito_Cuenta_Cliente_Negocio_Email(emailNegocio:String):List<Orden>
    fun findByCredito_Id(creditoId:Long):Optional<Orden>
}