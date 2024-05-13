package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.credito.Cuota
import com.example.finanzasbackend.model.credito.EstadoCuota
import com.example.finanzasbackend.repository.ClienteRepository
import com.example.finanzasbackend.repository.CuentaRepository
import com.example.finanzasbackend.repository.CuotaRepository
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service

@Service
class CuotaService(
        private val cuotaRepository: CuotaRepository,
        private val cuentaRepository: CuentaRepository
) {
    fun pagarCuota(cuentaId:Long,cuotaId:Long,metodoPago:String): Cuota {
        val cuotaFound = cuotaRepository.findById(cuotaId)
        if(cuotaFound.isEmpty)
            throw BadRequestException("Cuota no encontrada")

        val cuota = cuotaFound.get()
        if(cuota.estadoCuota == EstadoCuota.PAGADA)
            throw BadRequestException("La cuota ya ha sido pagada el ${cuota.fechaPago}")
        cuota.pagarCuota(metodoPago);

        val cuenta = cuentaRepository.findById(cuentaId).get()
        cuenta.recuperarLineaCredito(cuota.amortizacion)
        cuentaRepository.save(cuenta);

        return cuotaRepository.save(cuota);
    }
}