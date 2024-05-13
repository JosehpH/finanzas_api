package com.example.finanzasbackend.model.credito

import com.example.finanzasbackend.dto.cuota.CuotaResponse
import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.model.credito.gracia.PeriodoGracia
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteres
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresEfectiva
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresNominal
import com.example.finanzasbackend.model.credito.tasaInteres.TipoPeriodo
import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate
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

    @OneToMany(mappedBy = "credito",cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var cuotas:MutableList<Cuota> = mutableListOf()

    constructor(consumo:Orden, tasaCompensatoria: TasaInteres, tasaMoratoria: TasaInteres, pagoInicial: Float, fechaDesembolso: LocalDate, numCuotas: Int?, periodoPago: TipoPeriodo?, periodoGracia: PeriodoGracia?) : super(consumo, tasaCompensatoria, tasaMoratoria, pagoInicial,fechaDesembolso) {
        this.numCuotas = numCuotas
        this.periodoPago = periodoPago
        this.periodoGracia = periodoGracia
    }

    override fun calcularCuotas() {
        val tepCompemsatoria:TasaInteresEfectiva
        val tepMoratoria:TasaInteresEfectiva

        if(tasaCompensatoria is TasaInteresNominal)
            tepCompemsatoria = TasaNominalATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresNominal)
        else
            tepCompemsatoria = TasaEfectivaATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresEfectiva)

        if(tasaMoratoria is TasaInteresNominal)
            tepMoratoria = TasaNominalATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresNominal)
        else
            tepMoratoria = TasaEfectivaATasaEfectivaPeriodoPago(tasaCompensatoria as TasaInteresEfectiva)

        if(tipoAnualidad == TipoAnualidad.VENCIDA)
            calcularCuotasAnualidadVencida(tepCompemsatoria,tepMoratoria)
        else
            calcularCuotasAnualidadAdelantada()
        this.consumo!!.asignarToCredito(this)
    }
    private fun TasaNominalATasaEfectivaPeriodoPago(tasaNominal:TasaInteresNominal):TasaInteresEfectiva{
        val m:Double = tasaNominal.periodo.dias/tasaNominal.periodoCapitalizacion!!.dias.toDouble()
        val n:Double = periodoPago!!.dias/tasaNominal.periodoCapitalizacion!!.dias.toDouble()
        val tasaEfectiva:Double = (Math.pow((1+tasaNominal.tasa/m),n) - 1)*100
        return TasaInteresEfectiva(
                tasa = tasaEfectiva.toFloat(),
                periodo = periodoPago!!
        )
    }
    private fun TasaEfectivaATasaEfectivaPeriodoPago(tasaEfectiva:TasaInteresEfectiva):TasaInteresEfectiva{
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
        val i:Double = (tasaCompensatoria.tasa/100f).toDouble()
        val n:Double = numCuotas!!.toDouble()
        val anualidad:Double = saldoPendiente*(i*Math.pow((1+i),n))/(Math.pow((1+i),n)-1).toDouble()

        for (index in 1..n.toInt()){
            //LLevamos la anualidad a valor presente
            val diasTrasladar:Int = index*periodoPago!!.dias
            val diasTEP:Int = tasaCompensatoria.periodo.dias
            val amortizacion = anualidad/Math.pow((1+i),diasTrasladar/diasTEP.toDouble())

            val cuota:Cuota = Cuota(
                    fechaVencimiento = fechaDesembolso.plusDays(diasTrasladar.toLong()),
                    monto = anualidad.toFloat(),
                    interes = (anualidad-amortizacion).toFloat(),
                    amortizacion = amortizacion.toFloat(),
                    numeroCuota = index
            )
            cuota.asignarToCredito(this)
            agregarCuota(cuota)
        }
    }
    private fun calcularCuotasAnualidadAdelantada(){

    }
    fun agregarCuota(cuota:Cuota){
        cuotas.add(cuota)
    }
}