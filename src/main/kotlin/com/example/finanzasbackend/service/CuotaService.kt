package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Pago
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.Cuota
import com.example.finanzasbackend.model.credito.EstadoCuota
import com.example.finanzasbackend.repository.CreditoRepository
import com.example.finanzasbackend.repository.CuentaRepository
import com.example.finanzasbackend.repository.CuotaRepository
import org.apache.coyote.BadRequestException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CuotaService(
    private val cuotaRepository: CuotaRepository,
    private val cuentaRepository: CuentaRepository,
    private val creditoRepository: CreditoRepository,
    private val clienteRepository: CreditoRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(CuotaService::class.java)
    }
    fun pagarCuota(clienteId: Long, cuotaId: Long, metodoPago: String, creditoId: Long): Cuota {
        val cuotaFound = cuotaRepository.findById(cuotaId)
        if (cuotaFound.isEmpty)
            throw BadRequestException("Cuota no encontrada")

        val cuota = cuotaFound.get()
        if (cuota.estadoCuota == EstadoCuota.PAGADA)
            throw BadRequestException("La cuota ya ha sido pagada el ${cuota.fechaPago}")

        var pago: Pago = getInfoPago(cuotaId = cuotaId, creditoId = creditoId)
        cuota.pagarCuota(metodoPago, pago.cuotaActual, pago.fechaPago);

        val cuenta = clienteRepository.findById(clienteId).get().cuenta!!
        cuenta.recuperarLineaCredito(cuota.amortizacion)
        cuentaRepository.save(cuenta);

        return cuotaRepository.save(cuota);
    }

    fun getCuotaById(cuotaId: Long): Cuota {
        return cuotaRepository.findById(cuotaId).get()
    }

    fun getInfoPago(cuotaId: Long, creditoId: Long): Pago {
        val credito = creditoRepository.findById(creditoId).orElse(null)
        val cuota = cuotaRepository.findById(cuotaId).orElse(null)
        val now = LocalDate.now()
        var cuotaToValorPresente: Cuota? = null

        if (now.isEqual(cuota.fechaVencimiento) || now.isAfter(cuota.fechaVencimiento))
            return Pago(fechaPago = LocalDate.now(), cuotaActual = cuota.monto)

        cuotaToValorPresente = when (credito) {
            is CreditoValoFuturo -> {
                credito.cuotaToValorPresente()
            }

            else -> {
                (credito as CreditoAnualidad).cuotaToValorPresente(cuota)
            }
        }

        return Pago(
            fechaPago = cuotaToValorPresente!!.fechaVencimiento!!,
            cuotaActual = cuotaToValorPresente.monto,
        )
    }

}