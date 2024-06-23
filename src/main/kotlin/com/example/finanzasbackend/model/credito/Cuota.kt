package com.example.finanzasbackend.model.credito

import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate

@Entity
@Data
class Cuota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0
    var fechaVencimiento:LocalDate = LocalDate.now()
    var amortizacion:Float = 0f
    var interesCompensatorio:Float = 0f
    var interesCompensatorioMora:Float = 0f
    var interesMoratorio:Float=0f
    var monto:Float = 0f
    var numeroDeCuota:Int = 0

    var fechaPago:LocalDate? = null
    var metodoPago:String = ""
    var montoPagado:Float =0f
    /*@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credito_id")
    var credito:Credito?=null*/

    @Enumerated(EnumType.STRING)
    var estadoCuota:EstadoCuota? = EstadoCuota.PENDIENTE

    constructor(fechaVencimiento: LocalDate, amortizacion: Float, interes: Float, monto: Float,numeroCuota:Int, tasaCompensatoria:Float?=null, tasaMoratoria:Float?=null) {
        this.fechaVencimiento = fechaVencimiento
        this.amortizacion = amortizacion
        this.interesCompensatorio = interes
        this.monto = monto
        this.numeroDeCuota = numeroCuota
    }

    fun pagarCuota(metodoPago: String, cuotaActual: Float, fechaPago: LocalDate){
        this.metodoPago = metodoPago
        this.estadoCuota = EstadoCuota.PAGADA
        this.fechaPago = fechaPago
        this.montoPagado = cuotaActual
    }
//    fun asignarToCredito(credito:Credito){
//        this.credito = credito
//    }
}