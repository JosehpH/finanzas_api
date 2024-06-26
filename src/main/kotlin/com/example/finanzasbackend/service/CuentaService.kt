package com.example.finanzasbackend.service

import com.example.finanzasbackend.controller.cuenta.CuentaController
import com.example.finanzasbackend.model.Cliente
import com.example.finanzasbackend.model.Cuenta
import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.repository.ClienteRepository
import com.example.finanzasbackend.repository.CreditoRepository
import com.example.finanzasbackend.repository.CuentaRepository
import com.example.finanzasbackend.repository.NegocioRepository
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException.BadRequest
import java.util.*
import kotlin.math.log

@Service
class CuentaService(
        private val negocioRepository: NegocioRepository,
        private val clienteRepository: ClienteRepository,
        private val cuentaRepository: CuentaRepository,
        private val creditoRepository: CreditoRepository
) {
    companion object{
        private val logger:Logger = LoggerFactory.getLogger(CuentaService::class.java)
    }
    fun updateLimiteCrediticio(clienteId: Long, limiteNuevo:Float){
        val cliente = clienteRepository.findById(clienteId).get()
        cliente.cuenta!!.lineaCredito = limiteNuevo
        clienteRepository.save(cliente)
    }
    fun solicitarCredito(clienteId: Long, credito: Credito): Credito {
        val cliente = clientePerteneceAlNegocio(clienteId)
                ?: throw BadRequestException("El cliente no pertenece al negocio")

        if(cliente.cuenta!!.lineaCredito<credito.saldo-credito.pagoInicial)
            throw BadRequestException("La línea de crédito es insuficiente para realizar el credito")

        if(cliente.cuenta!!.hayPagosAtrasados())
            throw BadRequestException("El cliente presenta pagos atrasados, primero cancele esos pagos para solicitar un nuevo crédito")

        credito.calcularCuotas()
        cliente.cuenta!!.agregarCredito(credito)
        cliente.cuenta!!.reducirLineaCredito(credito.saldo-credito.pagoInicial)
        logger.info("Cantidad de creditos de la cuenta antes de guardad: ${cliente.cuenta!!.creditos.size}")
        clienteRepository.save(cliente)
        logger.info("Cantidad de creditos de la cuenta despues de guardad: ${cliente.cuenta!!.creditos.size}")
        return credito
    }

    fun getCuentaByClienteId(clienteId: Long): Cuenta? {
        val cliente = clientePerteneceAlNegocio(clienteId)
                ?: throw BadRequestException("El cliente no pertenece al negocio")
        logger.info("Cantidad de creditos: ${cliente.cuenta!!.creditos.size}")
        return cliente.cuenta
    }

    private fun clientePerteneceAlNegocio(clienteId: Long): Cliente? {
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        val foundNegocio: Optional<Negocio> = negocioRepository.findByEmail(negocioEmail)
        val negocio = foundNegocio.get()
        val cliente = clienteRepository.findById(clienteId)

        if (cliente.isEmpty)
            throw BadRequestException("Cliente no se ha encontrado")

        if (negocio.clientes.contains(cliente.get()))
            return cliente.get()
        else return null
    }


}