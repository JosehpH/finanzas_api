package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.repository.OrdenRepository
import org.apache.coyote.BadRequestException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class OrdenService(
        private val ordenRepository: OrdenRepository
) {
    fun crearOrden(orden: Orden):Orden{
        orden.items.forEach {
            it.addToOrden(orden)
        }
        return ordenRepository.save(orden)
    }
    fun getOrdenesByClienteId(clienteId:Long):List<Orden> {
        return ordenRepository.findByCredito_Cuenta_Cliente_Id(clienteId)
    }
    fun getOrdenesByNegocio():List<Orden>{
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        return ordenRepository.findByCredito_Cuenta_Cliente_Negocio_Email(negocioEmail)
    }
    fun getOrdenById(ordenId:Long):Orden{
        val found = ordenRepository.findById(ordenId)
        if(found.isEmpty)
            throw BadRequestException("La orden no existe")
        return found.get()
    }
    fun getOrdenByCreditoId(creditoId:Long):Orden{
        val found = ordenRepository.findByCredito_Id(creditoId)
        if(found.isEmpty)
            throw BadRequestException("El credito no se ha encontrado")
        return found.get()
    }

}