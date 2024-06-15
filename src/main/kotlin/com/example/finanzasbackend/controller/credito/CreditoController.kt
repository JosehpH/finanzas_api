package com.example.finanzasbackend.controller.credito;

import com.example.finanzasbackend.dto.clientes.ClienteResponse
import com.example.finanzasbackend.dto.credito.*
import com.example.finanzasbackend.dto.cuenta.CuentaResponse
import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.dto.gracia.GraciaResponse
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
import com.example.finanzasbackend.service.CreditoService
import com.example.finanzasbackend.service.OrdenService
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("api/creditos")
@Tag(name = "Creditos", description = "Controller para previsualizar los creditos antes de confirmar si estará ligado a la cuenta")
class CreditoController(
        private val creditoService: CreditoService,
        private val ordenService: OrdenService
) {
    @PostMapping("/valor-futuro")
    fun prevCreditoValorFuturo(@RequestBody request: CreditoValorFuturoRequest): ResponseEntity<CreditoValorFuturoResponse> {
        val response = creditoService.calcularCuotasValorFuturo(request.toCredito());
        return ResponseEntity(response.toCreditoValorFuturoResponse(),HttpStatus.OK);
    }

    @PostMapping("/anualidades")
    fun prevCreditoAnualidad(@RequestBody request: CreditoAnualidadesRequest): ResponseEntity<CreditoAnualidadesResponse> {
        val response = creditoService.calcularCuotasAnualidad(request.toCredito());
        return ResponseEntity(response.toResponse(),HttpStatus.OK);
    }

    @GetMapping("/")
    fun getAll(): List<CreditoWithClienteResponse?> {
        val lista = creditoService.getAllCreditos()
        return lista.map { it.toResponse() };
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id:Long):ResponseEntity<CreditoResponse?> {
        val detallesCredito = creditoService.getById(id)
        var creditoResponse:CreditoResponse?=null
        when(detallesCredito){
            is CreditoValoFuturo -> creditoResponse=detallesCredito.toCreditoValorFuturoResponse()
            is CreditoAnualidad -> creditoResponse = detallesCredito.toResponse()
        }
        return ResponseEntity(creditoResponse,HttpStatus.OK)
    }


    @GetMapping("/search")
    fun getByDni(@RequestParam dni: String): List<CreditoWithClienteResponse?> {
        val lista = creditoService.getByDni(dni)
        return lista.map {
            it.toResponse()
        }
    }
    @GetMapping("/filter")
    fun getTest(@RequestParam dni: String?, @RequestParam fechaInicio:LocalDate?, @RequestParam fechaFinal:LocalDate?):ResponseEntity<List<CreditoWithClienteResponse>>{
        val lista = creditoService.filter(dni,fechaInicio,fechaFinal)
        val response = lista.map { it.toResponse() }
        return ResponseEntity(response,HttpStatus.OK)
    }


    private fun Credito.toResponse(): CreditoWithClienteResponse =
            CreditoWithClienteResponse(
                    creditoId = this.id,
                    tipoCredito = if (this is CreditoValoFuturo) "VALOR FUTURO" else "ESTILO ANUALIDADES",
                    cliente = ClienteResponse(
                            id = this.cuenta!!.cliente!!.id,
                            nombres = this.cuenta!!.cliente!!.nombres,
                            email = this.cuenta!!.cliente!!.email,
                            apellidoPaterno = this.cuenta!!.cliente!!.apellidoPaterno,
                            apellidoMaterno = this.cuenta!!.cliente!!.apellidoMaterno,
                            dni = this.cuenta!!.cliente!!.dni,
                            telefono = this.cuenta!!.cliente!!.telefono,
                            cuenta = null,
                            photo = null
                    ),
                    estadoCredito = this.estadoCredito().toString(),
                    saldo = this.saldo,
                    pagoInicial = this.pagoInicial,
                    saldoRestante = this.saldo - this.pagoInicial,
                    fechaDesembolso = this.fechaDesembolso,
                    consumoId = this.consumo!!.id
            )

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
                id = this.id,
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
            if (it is CreditoValoFuturo)
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

    private fun CreditoAnualidadesRequest.toCredito(): CreditoAnualidad {
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
        val periodoGracia: PeriodoGracia?
        if (this.gracia == null)
            periodoGracia = null
        else if (this.gracia.tipo == TipoPeriodoGracia.TOTAL.name)
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

    private fun CreditoAnualidad.toResponse(): CreditoAnualidadesResponse {
        //Mapeo de cuotas a Response
        val cuotas: List<CuotaResponse> = this.cuotas.map {
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
        var gracia: GraciaResponse? = null
        if (this.periodoGracia != null) {
            var tipo: String
            if (this.periodoGracia!! is PeriodoGraciaTotal)
                tipo = TipoPeriodoGracia.TOTAL.name
            else
                tipo = TipoPeriodoGracia.PARCIAL.name

            gracia = GraciaResponse(
                    id = this.periodoGracia!!.id,
                    numCuotas = this.periodoGracia!!.numCuotas.toLong(),
                    tipo = tipo,
                    saldoRestante = this.periodoGracia!!.saldoPendiente
            )
        }
        return CreditoAnualidadesResponse(
                id = this.id,
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
                periodoGracia = gracia,
                numCuotas = this.numCuotas!!.toInt(),
                cuotas = cuotas
        )
    }
}
