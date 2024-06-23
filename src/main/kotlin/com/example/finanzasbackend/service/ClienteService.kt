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

    fun updateTelefono(clienteId: Long,telefono:String):Cliente{
       val cliente = clienteRepository.findById(clienteId).get()
        cliente.telefono = telefono
        clienteRepository.save(cliente)
        return cliente
    }
    fun updateEmail(clienteId: Long,email:String):Cliente{
        val cliente = clienteRepository.findById(clienteId).get()
        cliente.email = email
        clienteRepository.save(cliente)
        return cliente
    }


    fun getAll() = clienteRepository.findAll()

    fun getAllByNegocio():List<Cliente> {
        val negocioEmail =  SecurityContextHolder.getContext().authentication.name
        val foundNegocio:Optional<Negocio> = negocioRepository.findByEmail(negocioEmail)
        val negocio = foundNegocio.get()
        return negocio.clientes
    }

    fun getById(id:Long):Cliente{
        val found = clienteRepository.findById(id);
        if(found.isEmpty)
            throw BadRequestException("El usuario con id: $id no existe")
        return found.get()
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
    fun findByKeyword(keyword:String) = clienteRepository.findByKeyword(keyword)
    fun getByDni(dni:String):Cliente{
        val found = clienteRepository.findByDni(dni);
        if(found.isEmpty)
            throw BadRequestException("El usuario con dni: $dni no existe")
        return found.get()
    }

    fun getIdByDniAndNegocioRuc(dni:String, rucNegocio:String):Long{
        val cliente:Cliente? = clienteRepository.findByDniAndNegocio_Ruc(dni,rucNegocio).orElse(null)
        if(cliente==null)
            throw BadRequestException("El cliente no se ha encontrado para este negocio")
        else
            return cliente.id
    }
}