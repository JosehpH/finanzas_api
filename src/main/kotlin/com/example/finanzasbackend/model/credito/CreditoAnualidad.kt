package com.example.finanzasbackend.model.credito

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.model.credito.gracia.PeriodoGracia
import com.example.finanzasbackend.model.credito.gracia.PeriodoGraciaParcial
import com.example.finanzasbackend.model.credito.gracia.PeriodoGraciaTotal
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteres
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresEfectiva
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresNominal
import com.example.finanzasbackend.model.credito.tasaInteres.TipoPeriodo
import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

@Entity
@Data
@DiscriminatorValue(value = "ANUALIDAD")
class CreditoAnualidad:Credito {
    var numCuotas:Int? = 0

    @Enumerated(EnumType.STRING)
    var tipoAnualidad:TipoAnualidad = TipoAnualidad.VENCIDA

    var periodoPago: TipoPeriodo? = TipoPeriodo.MENSUAL

    @OneToOne(cascade = [CascadeType.ALL])
    var periodoGracia: PeriodoGracia? = null

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    @JoinColumn(name = "credito_id")
    var cuotas:MutableList<Cuota> = mutableListOf()

    constructor(consumo:Orden, tasaCompensatoria: TasaInteres, tasaMoratoria: TasaInteres, pagoInicial: Float, fechaDesembolso: LocalDate, numCuotas: Int?, periodoPago: TipoPeriodo?, periodoGracia: PeriodoGracia?) : super(consumo, tasaCompensatoria, tasaMoratoria, pagoInicial,fechaDesembolso) {
        this.numCuotas = numCuotas
        this.periodoPago = periodoPago
        this.periodoGracia = periodoGracia
    }

    fun cuotaToValorPresente(cuota:Cuota):Cuota{
        var tasaEfectiva:TasaInteresEfectiva
        var diasTranscurridos:Long = calcularDiasEntreFechas(LocalDate.now(),cuota.fechaVencimiento)
        when(tasaCompensatoria){
            is TasaInteresNominal-> tasaEfectiva = tasaNominalATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresNominal)
            else-> tasaEfectiva = tasaEfectivaATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresEfectiva)
        }
        var TED:Double = Math.pow((1+tasaEfectiva.tasa/100*1.0),(1/tasaEfectiva.periodo.dias*1.0)) -1
        var valorActual:Double = cuota.monto/Math.pow((1.0+TED),diasTranscurridos*1.0)
        return Cuota(
            numeroCuota = 1,
            monto = valorActual.toFloat(),
            amortizacion = cuota.amortizacion.toFloat(),
            interes = (valorActual-cuota.amortizacion).toFloat(),
            fechaVencimiento = LocalDate.now()
        )
    }

    override fun calcularCuotas() {
        val tepCompemsatoria:TasaInteresEfectiva
        val tepMoratoria:TasaInteresEfectiva

        if(tasaCompensatoria is TasaInteresNominal)
            tepCompemsatoria = tasaNominalATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresNominal)
        else
            tepCompemsatoria = tasaEfectivaATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresEfectiva)

        if(tasaMoratoria is TasaInteresNominal)
            tepMoratoria = tasaNominalATasaEfectivaPeriodoPago(tasaMoratoria as TasaInteresNominal)
        else
            tepMoratoria = tasaEfectivaATasaEfectivaPeriodoPago(tasaMoratoria as TasaInteresEfectiva)

        if(this.periodoGracia==null)
            calcularCuotasAnualidadVencida(tepCompemsatoria,tepMoratoria)
        else if(this.periodoGracia is PeriodoGraciaTotal)
            calcularCuotasAnualidadVencidaGraciaTotal(tepCompemsatoria,tepMoratoria)
        else
            calcularCuotasAnualidadVencidaGraciaParcial(tepCompemsatoria,tepMoratoria)

        this.consumo!!.asignarToCredito(this)
    }

    override fun calcularMora() {
        println("Anualidad Mora")
    }

    override fun estadoCredito(): EstadoCuota {
        val pagosAtrasados:Boolean =  cuotas.any{ it.estadoCuota == EstadoCuota.ATRASADA}
        val cuotasPagadas:Boolean = cuotas.all { it.estadoCuota==EstadoCuota.PAGADA }
        if(pagosAtrasados) return EstadoCuota.ATRASADA
        else if(cuotasPagadas) return EstadoCuota.PAGADA
        else return EstadoCuota.PENDIENTE
    }

    private fun tasaNominalATasaEfectivaPeriodoPago(tasaNominal:TasaInteresNominal):TasaInteresEfectiva{

        val m:Double = tasaNominal.periodo.dias/tasaNominal.periodoCapitalizacion!!.dias.toDouble()

        val n:Double = periodoPago!!.dias/tasaNominal.periodoCapitalizacion!!.dias.toDouble()

        val tasaEfectiva:Double = (Math.pow((1+tasaNominal.tasa/(100*m)),n) - 1)*100

        return TasaInteresEfectiva(
                tasa = tasaEfectiva.toFloat(),
                periodo = periodoPago!!
        )
    }
    private fun tasaEfectivaATasaEfectivaPeriodoPago(tasaEfectiva:TasaInteresEfectiva):TasaInteresEfectiva{
        val n2:Double = periodoPago!!.dias.toDouble()
        val n1:Double = tasaEfectiva.periodo.dias.toDouble()
        val tasaEfectiva2 = Math.pow((1+tasaEfectiva.tasa).toDouble(),(n2/n1))
        return TasaInteresEfectiva(
                tasa = tasaEfectiva2.toFloat(),
                periodo = periodoPago!!
        )
    }
    private fun calcularCuotasAnualidadVencida(tepCompensatoria:TasaInteresEfectiva,tepMoratoria:TasaInteresEfectiva){
        val saldoPendiente = saldo-pagoInicial
        val i:Double = (tepCompensatoria.tasa/100f).toDouble()
        val n:Double = numCuotas!!.toDouble()
        //var anualidad:Double = saldoPendiente*(i*Math.pow((1+i),n))/(Math.pow((1+i),n)-1).toDouble()
        var anualidad:Double = ((saldoPendiente*i)/(1-Math.pow((1+i),-n)))
        println("Anualidad: $anualidad")
        println("Saldo pendiente: $saldoPendiente")
        println("i: $i")
        println("n: $n")

        anualidad = roundTo2Decimals(anualidad.toFloat()).toDouble()

        for (index in 1..n.toInt()){
            //LLevamos la anualidad a valor presente
            val diasTrasladar:Int = index*periodoPago!!.dias
            val diasTEP:Int = tasaCompensatoria.periodo.dias
            val amortizacion:Double = anualidad/Math.pow((1+i),diasTrasladar/diasTEP.toDouble())
            val cuota:Cuota = Cuota(
                    fechaVencimiento = fechaDesembolso.plusDays(diasTrasladar.toLong()),
                    monto = roundTo2Decimals(anualidad.toFloat()),
                    interes = roundTo2Decimals((anualidad-amortizacion).toFloat()),
                    amortizacion =roundTo2Decimals(amortizacion.toFloat()),
                    numeroCuota = index
            )
            interesAcumulado+=cuota.interesCompensatorio
            agregarCuota(cuota)
        }
    }
    private fun calcularCuotasAnualidadVencidaGraciaTotal(tepCompensatoria:TasaInteresEfectiva,tepMoratoria:TasaInteresEfectiva){
        val i:Double = (tasaCompensatoria.tasa/100f).toDouble()
        val n:Double = numCuotas!!.toDouble()
        val diasGracia = periodoGracia!!.numCuotas*periodoPago!!.dias
        val saldoPendiente = (saldo-pagoInicial)*Math.pow((1+i),(diasGracia)/tepCompensatoria.periodo.dias.toDouble())
        this.periodoGracia!!.saldoPendiente = saldoPendiente.toFloat()
        interesAcumulado+=(saldoPendiente-(saldo-pagoInicial)).toFloat()

        val periodosRestantes:Double = n - periodoGracia!!.numCuotas

        val anualidad:Double = saldoPendiente*(i*Math.pow((1+i),periodosRestantes))/(Math.pow((1+i),periodosRestantes)-1).toDouble()

        for (index in 1..periodosRestantes.toInt()){
            //LLevamos la anualidad a valor presente
            val diasTrasladar:Int = index*periodoPago!!.dias
            val diasTEP:Int = tasaCompensatoria.periodo.dias
            val amortizacion = anualidad/Math.pow((1+i),diasTrasladar/diasTEP.toDouble())

            val cuota:Cuota = Cuota(
                    fechaVencimiento = fechaDesembolso.plusDays(diasTrasladar.toLong()+diasGracia),
                    monto = roundTo2Decimals(anualidad.toFloat()),
                    interes = roundTo2Decimals((anualidad-amortizacion).toFloat()),
                    amortizacion = roundTo2Decimals(amortizacion.toFloat()),
                    numeroCuota = index+periodoGracia!!.numCuotas
            )
            interesAcumulado+=cuota.interesCompensatorio

            agregarCuota(cuota)
        }
    }
    private fun calcularCuotasAnualidadVencidaGraciaParcial(tepCompensatoria:TasaInteresEfectiva,tepMoratoria:TasaInteresEfectiva){
        val i:Double = (tasaCompensatoria.tasa/100f).toDouble()
        val n:Double = numCuotas!!.toDouble()
        val diasGracia = periodoGracia!!.numCuotas*periodoPago!!.dias
        val saldoPendiente = saldo-pagoInicial
        this.periodoGracia!!.saldoPendiente = saldoPendiente.toFloat()

        //Periodo de gracia parcial cuotas
        val tiempoGracia = periodoPago!!.dias
        val periodoTasa  = tasaCompensatoria.periodo.dias
        val valorFuturoPeriodo = saldoPendiente*Math.pow((1+i),(tiempoGracia/periodoTasa.toDouble()))
        val interesGraciaPeriodo = valorFuturoPeriodo-saldoPendiente

        for ( index in 1..periodoGracia!!.numCuotas) {
            val diasTrasladar:Int = index*periodoPago!!.dias

            val cuota: Cuota = Cuota(
                    fechaVencimiento = fechaDesembolso.plusDays(diasTrasladar.toLong()),
                    monto = roundTo2Decimals(interesGraciaPeriodo.toFloat()),
                    interes = roundTo2Decimals(interesGraciaPeriodo.toFloat()),
                    amortizacion = 0f,
                    numeroCuota = index
                    )
            interesAcumulado+=cuota.interesCompensatorio
            agregarCuota(cuota)
        }
                //Calculo de cuotas vencidas
        val anualidad:Double = saldoPendiente*(i*Math.pow((1+i),n))/(Math.pow((1+i),n)-1).toDouble()
        for (index in 1..n.toInt()-periodoGracia!!.numCuotas){
            //LLevamos la anualidad a valor presente
            val diasTrasladar:Int = index*periodoPago!!.dias
            val diasTEP:Int = tasaCompensatoria.periodo.dias
            val amortizacion = anualidad/Math.pow((1+i),diasTrasladar/diasTEP.toDouble())

            val cuota:Cuota = Cuota(
                    fechaVencimiento = fechaDesembolso.plusDays(diasTrasladar.toLong()+diasGracia),
                    monto = roundTo2Decimals(anualidad.toFloat()),
                    interes = roundTo2Decimals((anualidad-amortizacion).toFloat()),
                    amortizacion = roundTo2Decimals(amortizacion.toFloat()),
                    numeroCuota = index+periodoGracia!!.numCuotas
            )
            interesAcumulado+=cuota.interesCompensatorio
            agregarCuota(cuota)
        }
    }
    private fun calcularDiasEntreFechas(fechaInicio:LocalDate,fechaFin:LocalDate):Long{
        return ChronoUnit.DAYS.between(fechaInicio,fechaFin);
    }

    fun agregarCuota(cuota:Cuota){
        cuotas.add(cuota)
        //cuota.asignarToCredito(this)
    }
}