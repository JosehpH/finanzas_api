package com.example.finanzasbackend.model.credito

import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteres
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresEfectiva
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresNominal
import jakarta.persistence.*
import lombok.Data
import org.springframework.cglib.core.Local
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.math.roundToInt

@Entity
@Data
@DiscriminatorValue(value = "VALOR FUTURO")
class CreditoValoFuturo:Credito{
    @OneToOne(cascade = [CascadeType.ALL])
    var cuota:Cuota? = null
    var fechaVencimiento:LocalDate = LocalDate.now()

    constructor(consumo: Orden, tasaCompensatoria: TasaInteres, tasaMoratoria: TasaInteres, pagoInicial: Float, fechaDesembolso: LocalDate,fechaVencimiento:LocalDate) : super(consumo, tasaCompensatoria, tasaMoratoria, pagoInicial, fechaDesembolso) {
        this.fechaVencimiento = fechaVencimiento
    }

    override fun calcularCuotas() {
        val plazoDiasCredito = calcularDiasEntreFechas()
        var valorFuturo:Float = 0f
        var saldoRestante = saldo-pagoInicial
        saldoRestante = roundTo2Decimals(saldoRestante)

        if(tasaCompensatoria is TasaInteresNominal){
            val capitalizacion = (tasaCompensatoria as TasaInteresNominal).periodoCapitalizacion?.dias
            val n:Double = (plazoDiasCredito*1.0)/(capitalizacion!!*1.0)
            val m = tasaCompensatoria.periodo.dias/capitalizacion.toFloat()
            val tasa = tasaCompensatoria.tasa/100f

            valorFuturo = (saldoRestante*Math.pow((1+tasa/m).toDouble(),n)).toFloat()
            valorFuturo = roundTo2Decimals(valorFuturo)
        }
        else if(tasaCompensatoria is TasaInteresEfectiva){
            val tasa = tasaCompensatoria.tasa/100f
            val periodoTasaDias = tasaCompensatoria.periodo.dias
            valorFuturo = (saldoRestante*Math.pow((1+tasa).toDouble(),(plazoDiasCredito/periodoTasaDias.toFloat()).toDouble())).toFloat()
            valorFuturo = roundTo2Decimals(valorFuturo)
        } else {
            //TODO
        }
        cuota = Cuota(
                fechaVencimiento = fechaVencimiento,
                amortizacion = saldoRestante,
                interes = roundTo2Decimals(valorFuturo-saldoRestante),
                monto = valorFuturo,
                numeroCuota = 1
        )
        //this.cuota!!.asignarToCredito(this)
        this.consumo!!.asignarToCredito(this)
    }

    override fun calcularMora() {
        calcularInteresCompensatorioMora()
        calcularInteresMoratorio()
    }

    override fun estadoCredito(): EstadoCuota {
        return cuota!!.estadoCuota!!
    }

    private fun calcularInteresMoratorio(){
        if(fechaVencimiento.isAfter(LocalDate.now())) return

        val diasTrasladar = calcularDiasEntreFechas(fechaVencimiento, LocalDate.now())
        var im:Float = 0f
        if(tasaMoratoria is TasaInteresNominal){
            val capitalizacion = (tasaMoratoria as TasaInteresNominal).periodoCapitalizacion?.dias
            val n:Double = (diasTrasladar*1.0)/(capitalizacion!!*1.0)
            val m = tasaMoratoria.periodo.dias/capitalizacion.toFloat()
            val tasa = tasaMoratoria.tasa/100f

            im = (cuota!!.monto*(Math.pow((1+tasa/m).toDouble(),n))-1).toFloat()
        }
        else if(tasaMoratoria is TasaInteresEfectiva){
            val tasa = tasaMoratoria.tasa/100f
            val periodoTasaDias = tasaMoratoria.periodo.dias
            im = (cuota!!.monto*(Math.pow((1+tasa).toDouble(),(diasTrasladar/periodoTasaDias.toFloat()).toDouble())-1)).toFloat()
        }
        cuota!!.interesMoratorio = im
    }
    private fun calcularInteresCompensatorioMora(){
        if(fechaVencimiento.isAfter(LocalDate.now()) || cuota!!.estadoCuota == EstadoCuota.PAGADA) return
        cuota!!.estadoCuota = EstadoCuota.ATRASADA

        val diasTrasladar = calcularDiasEntreFechas(fechaVencimiento, LocalDate.now())
        var icm:Float = 0f
        if(tasaCompensatoria is TasaInteresNominal){
            val capitalizacion = (tasaCompensatoria as TasaInteresNominal).periodoCapitalizacion?.dias
            val n:Double = (diasTrasladar*1.0)/(capitalizacion!!*1.0)
            val m = tasaCompensatoria.periodo.dias/capitalizacion.toFloat()
            val tasa = tasaCompensatoria.tasa/100f

            icm = (cuota!!.monto*(Math.pow((1+tasa/m).toDouble(),n))-1).toFloat()
        }
        else if(tasaCompensatoria is TasaInteresEfectiva){
            val tasa = tasaCompensatoria.tasa/100f
            val periodoTasaDias = tasaCompensatoria.periodo.dias
            icm = (cuota!!.monto*(Math.pow((1+tasa).toDouble(),(diasTrasladar/periodoTasaDias.toFloat()).toDouble())-1)).toFloat()
        }
        cuota!!.interesCompensatorioMora = icm
    }

    private fun calcularDiasEntreFechas():Long{
        return ChronoUnit.DAYS.between(this.fechaDesembolso,this.fechaVencimiento)
    }
    private fun calcularDiasEntreFechas(fechaInicio:LocalDate,fechaFin:LocalDate):Long{
        return ChronoUnit.DAYS.between(fechaInicio,fechaFin);
    }
}