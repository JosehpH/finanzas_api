package com.example.finanzasbackend.model.credito.gracia

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo_periodo",
        discriminatorType = DiscriminatorType.STRING
)
abstract class PeriodoGracia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0
    var numCuotas:Int = 0
    var saldoPendiente:Float=0f
    constructor(numCuotas: Int) {
        this.numCuotas = numCuotas
    }
}