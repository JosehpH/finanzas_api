package com.example.finanzasbackend.model.credito.tasaInteres

import jakarta.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo_tasa_interes",
        discriminatorType = DiscriminatorType.STRING
)
abstract class TasaInteres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @Enumerated(EnumType.STRING)
    var periodo: TipoPeriodo = TipoPeriodo.DIARIO

    var tasa:Float=0f

    constructor(periodo: TipoPeriodo, tasa: Float) {
        this.periodo = periodo
        this.tasa = tasa
    }
}