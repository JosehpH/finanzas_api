package com.example.finanzasbackend.model.credito.tasaInteres

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue(value = "EFECTIVA")
class TasaInteresEfectiva(periodo: TipoPeriodo, tasa: Float) : TasaInteres(periodo, tasa){

}