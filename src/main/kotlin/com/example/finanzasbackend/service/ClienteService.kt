package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Cliente
import com.example.finanzasbackend.model.Cuenta
import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.repository.ClienteRepository
import com.example.finanzasbackend.repository.NegocioRepository
import org.apache.coyote.BadRequestException
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ClienteService(
        private val clienteRepository: ClienteRepository,
        private val negocioRepository: NegocioRepository
) {

    fun getAll() = clienteRepository.findAll()

    fun getAllByNegocio():List<Cliente> {
        val negocioEmail =  SecurityContextHolder.getContext().authentication.name
        val foundNegocio:Optional<Negocio> = negocioRepository.findByEmail(negocioEmail)
        val negocio = foundNegocio.get()
        return negocio.clientes
    }

    fun registrarCliente(cliente:Cliente):Cliente?{
        val negocioEmail =  SecurityContextHolder.getContext().authentication.name
        val foundNegocio:Optional<Negocio> = negocioRepository.findByEmail(negocioEmail)
        val negocio = foundNegocio.get()

        cliente.negocio = negocio
        negocio.registrarCliente(cliente)
        negocioRepository.save(negocio)

        return cliente
    }

    fun aperturarCuenta(clienteId:Long,cuenta:Cuenta):Cliente{
        val negocioEmail =  SecurityContextHolder.getContext().authentication.name
        val foundNegocio:Optional<Negocio> = negocioRepository.findByEmail(negocioEmail)
        val negocio = foundNegocio.get()
        val cliente:Optional<Cliente> = clienteRepository.findById(clienteId)
        if(negocio.clientes.contains(cliente.get())) {
            cliente.get().aperturarCuenta(cuenta)
            clienteRepository.save(cliente.get())
            return cliente.get()
        }
        throw BadRequestException("Cliente no encontrado del negocio ${negocio.nombre}")
    }


}