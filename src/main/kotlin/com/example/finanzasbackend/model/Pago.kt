package com.example.finanzasbackend.model

import com.example.finanzasbackend.model.credito.Cuota
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToOne
import lombok.Data
import java.time.LocalDate

@Entity
@Data
class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @OneToOne
    @JoinColumn(name = "cuota_id")
    var cuota:Cuota? = null

    var metodoPago:String? = null

    var fechaPago:LocalDate = LocalDate.now()

    var cuotaActual:Float = 0f

    constructor(cuota: Cuota?=null, metodoPago: String?=null, fechaPago: LocalDate, cuotaActual: Float) {
        this.cuota = cuota
        this.metodoPago = metodoPago
        this.fechaPago = fechaPago
        this.cuotaActual = cuotaActual
    }
}