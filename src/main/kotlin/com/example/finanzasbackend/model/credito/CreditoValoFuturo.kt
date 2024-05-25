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
    private fun calcularDiasEntreFechas():Long{
        return ChronoUnit.DAYS.between(this.fechaDesembolso,this.fechaVencimiento)
    }
}