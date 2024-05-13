package com.example.finanzasbackend.controller.cuenta

import com.example.finanzasbackend.dto.credito.*
import com.example.finanzasbackend.dto.cuenta.*
import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.dto.tasa.TasaRequest
import com.example.finanzasbackend.model.Cuenta
import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.gracia.PeriodoGracia
import com.example.finanzasbackend.model.credito.gracia.PeriodoGraciaParcial
import com.example.finanzasbackend.model.credito.gracia.PeriodoGraciaTotal
import com.example.finanzasbackend.model.credito.gracia.TipoPeriodoGracia
import com.example.finanzasbackend.model.credito.tasaInteres.*
import com.example.finanzasbackend.service.CuentaService
import com.example.finanzasbackend.service.OrdenService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuenta", description = "Controlador de cuentas")
class CuentaController(
        private val cuentaService: CuentaService,
        private val ordenService: OrdenService
) {
    @Operation(summary = "Solicitar credito de estilo valor futuro a tasas nominales o efectivas")
    @PostMapping("/{clienteId}/solicitar-credito-valor-futuro")
    fun solicitarCreditoValorFuturo(@PathVariable clienteId: Long, @RequestBody request: CreditoValorFuturoRequest): ResponseEntity<CreditoValorFuturoResponse> {
        val credito = cuentaService.solicitarCredito(clienteId, request.toCredito())
        return ResponseEntity((credito as CreditoValoFuturo).toCreditoValorFuturoResponse(), HttpStatus.OK)

    }

    @Operation(summary = "Solicitar credito de estilo anualidades con o sin periodo de gracia")
    @PostMapping("/{clienteId}/solicitar-credito-anualidades")
    fun solicitarCreditoAnualidades(@PathVariable clienteId: Long, @RequestBody request: CreditoAnualidadesRequest): ResponseEntity<CreditoAnualidadesResponse> {
        val credito = cuentaService.solicitarCredito(clienteId, request.toCredito())
        return ResponseEntity((credito as CreditoAnualidad).toResponse(), HttpStatus.OK)

    }

    @Operation(summary = "Obtener cuenta de un cliente con sus respectivos creditos")
    @GetMapping("/{clienteId}")
    fun getCuentaByClienteId(@PathVariable clienteId: Long): ResponseEntity<CuentaResponse> {
        val cuenta = cuentaService.getCuentaByClienteId(clienteId)
                ?: throw Exception("El cliente no tiene una cuenta aperturada")
        return ResponseEntity(cuenta.toCuentaResponse(), HttpStatus.OK)
    }


    private fun CreditoValorFuturoRequest.toCredito(): CreditoValoFuturo {
        val tasac: TasaInteres
        if (this.tasaCompensatoria.tipo == TipoTasaInteres.NOMINAL.name)
            tasac = TasaInteresNominal(
                    periodo = TipoPeriodo.valueOf(this.tasaCompensatoria.periodo),
                    tasa = this.tasaCompensatoria.tasa,
                    periodoCapitalizacion = TipoPeriodo.valueOf(this.tasaCompensatoria.periodoCapitalizacion!!)
            )
        else tasac = TasaInteresEfectiva(
                periodo = TipoPeriodo.valueOf(this.tasaCompensatoria.periodo),
                tasa = this.tasaCompensatoria.tasa
        )

        val tasam: TasaInteres
        if (this.tasaMoratoria.tipo == TipoTasaInteres.NOMINAL.name)
            tasam = TasaInteresNominal(
                    periodo = TipoPeriodo.valueOf(this.tasaMoratoria.periodo),
                    tasa = this.tasaMoratoria.tasa,
                    periodoCapitalizacion = TipoPeriodo.valueOf(this.tasaMoratoria.periodoCapitalizacion!!)

            )
        else tasam = TasaInteresEfectiva(
                periodo = TipoPeriodo.valueOf(this.tasaMoratoria.periodo),
                tasa = this.tasaMoratoria.tasa
        )
        return CreditoValoFuturo(
                consumo = ordenService.getOrdenById(this.ordenId),
                pagoInicial = this.pagoInicial,
                fechaDesembolso = this.fechaDesembolso,
                fechaVencimiento = this.fechaVencimiento,
                tasaCompensatoria = tasac,
                tasaMoratoria = tasam,

                )
    }
    private fun CreditoValoFuturo.toCreditoValorFuturoResponse(): CreditoValorFuturoResponse {
        return CreditoValorFuturoResponse(
                id=this.id,
                tipoCredito = "VALOR FUTURO",
                saldo = this.saldo,
                pagoInicial = this.pagoInicial,
                saldoRestante = this.saldo - this.pagoInicial,
                tasaCompensatoria = TasaRequest(
                        periodo = this.tasaCompensatoria.periodo.name,
                        tasa = this.tasaCompensatoria.tasa,
                        tipo = if (this.tasaCompensatoria is TasaInteresNominal)
                            TipoTasaInteres.NOMINAL.name
                        else TipoTasaInteres.EFECTIVA.name,
                        periodoCapitalizacion = TipoPeriodo.DIARIO.name

                ),
                tasaMoratoria = TasaRequest(
                        periodo = this.tasaMoratoria.periodo.name,
                        tasa = this.tasaMoratoria.tasa,
                        tipo = if (this.tasaMoratoria is TasaInteresNominal)
                            TipoTasaInteres.NOMINAL.name
                        else TipoTasaInteres.EFECTIVA.name,
                        periodoCapitalizacion = TipoPeriodo.DIARIO.name
                ),
                fechaDesembolso = this.fechaDesembolso,
                cuota = CuotaResponse(
                        id = this.cuota!!.id,
                        fechaVencimiento = this.cuota!!.fechaVencimiento,
                        amortizacion = this.cuota!!.amortizacion,
                        interesMoratorio = this.cuota!!.interesMoratorio,
                        interesCompensatorioMora = this.cuota!!.interesCompensatorioMora,
                        interesCompensatorio = this.cuota!!.interesCompensatorio,
                        monto = this.cuota!!.monto,
                        numeroCuota = this.cuota!!.numeroDeCuota,
                        fechaPago = this.cuota!!.fechaPago,
                        metodoPago = this.cuota!!.metodoPago,
                        estadoCuota = this.cuota!!.estadoCuota?.name
                )
        )
    }
    private fun Cuenta.toCuentaResponse(): CuentaResponse {
        val creditos: List<CreditoResponse?> = this.creditos.map {
            if(it is CreditoValoFuturo)
                it.toCreditoValorFuturoResponse()
            else
                (it as CreditoAnualidad).toResponse()


        }
        return CuentaResponse(
                id = this.id,
                limiteCrediticio = this.lineaCredito,
                creditos = creditos
        )
    }
    private fun CreditoAnualidadesRequest.toCredito():CreditoAnualidad{
        //Tasa compensatoria a entidad
        val tasac: TasaInteres
        if (this.tasaCompensatoria.tipo == TipoTasaInteres.NOMINAL.name)
            tasac = TasaInteresNominal(
                    periodo = TipoPeriodo.valueOf(this.tasaCompensatoria.periodo),
                    tasa = this.tasaCompensatoria.tasa,
                    periodoCapitalizacion = TipoPeriodo.valueOf(this.tasaCompensatoria.periodoCapitalizacion!!)
            )
        else tasac = TasaInteresEfectiva(
                periodo = TipoPeriodo.valueOf(this.tasaCompensatoria.periodo),
                tasa = this.tasaCompensatoria.tasa
        )

        //Tasa moratoria a entidad
        val tasam: TasaInteres
        if (this.tasaMoratoria.tipo == TipoTasaInteres.NOMINAL.name)
            tasam = TasaInteresNominal(
                    periodo = TipoPeriodo.valueOf(this.tasaMoratoria.periodo),
                    tasa = this.tasaMoratoria.tasa,
                    periodoCapitalizacion = TipoPeriodo.valueOf(this.tasaMoratoria.periodoCapitalizacion!!)

            )
        else tasam = TasaInteresEfectiva(
                periodo = TipoPeriodo.valueOf(this.tasaMoratoria.periodo),
                tasa = this.tasaMoratoria.tasa
        )

        //Periodo de gracia a entidad
        val periodoGracia:PeriodoGracia?
        if(this.gracia==null)
            periodoGracia = null
        else if(this.gracia.tipo == TipoPeriodoGracia.TOTAL.name)
            periodoGracia = PeriodoGraciaTotal(numCuotas = this.gracia.numCuotas)
        else if (this.gracia.tipo == TipoPeriodoGracia.PARCIAL.name)
            periodoGracia = PeriodoGraciaParcial(numCuotas = this.gracia.numCuotas)
        else
            periodoGracia = null

        return CreditoAnualidad(
                consumo = ordenService.getOrdenById(this.ordenId),
                pagoInicial = this.pagoInicial,
                fechaDesembolso = this.fechaDesembolso,
                tasaCompensatoria = tasac,
                tasaMoratoria = tasam,
                numCuotas = this.numCuotas,
                periodoPago = TipoPeriodo.valueOf(this.periodoPago),
                periodoGracia = periodoGracia
        )
    }
    private fun CreditoAnualidad.toResponse():CreditoAnualidadesResponse{
        //Mapeo de cuotas a Response
        val cuotas:List<CuotaResponse> = this.cuotas.map {
            CuotaResponse(
                    id = it.id,
                    fechaVencimiento = it.fechaVencimiento,
                    amortizacion = it.amortizacion,
                    interesCompensatorio = it.interesCompensatorio,
                    monto = it.monto,
                    numeroCuota = it.numeroDeCuota,
                    fechaPago = it.fechaPago,
                    metodoPago = it.metodoPago,
                    estadoCuota = it.estadoCuota?.name,
                    interesMoratorio = it.interesMoratorio,
                    interesCompensatorioMora = it.interesCompensatorioMora
            )
        }

        return CreditoAnualidadesResponse(
                id=this.id,
                tipoCredito = "ANUALIDADES",
                saldo = this.saldo,
                pagoInicial = this.pagoInicial,
                saldoRestante = this.saldo - this.pagoInicial,
                tasaCompensatoria = TasaRequest(
                        periodo = this.tasaCompensatoria.periodo.name,
                        tasa = this.tasaCompensatoria.tasa,
                        tipo = if (this.tasaCompensatoria is TasaInteresNominal)
                            TipoTasaInteres.NOMINAL.name
                        else TipoTasaInteres.EFECTIVA.name,
                        periodoCapitalizacion = TipoPeriodo.DIARIO.name

                ),
                tasaMoratoria = TasaRequest(
                        periodo = this.tasaMoratoria.periodo.name,
                        tasa = this.tasaMoratoria.tasa,
                        tipo = if (this.tasaMoratoria is TasaInteresNominal)
                            TipoTasaInteres.NOMINAL.name
                        else TipoTasaInteres.EFECTIVA.name,
                        periodoCapitalizacion = TipoPeriodo.DIARIO.name
                ),
                fechaDesembolso = this.fechaDesembolso,
                numCuotas = this.numCuotas!!.toInt(),
                cuotas = cuotas
        )
    }
}