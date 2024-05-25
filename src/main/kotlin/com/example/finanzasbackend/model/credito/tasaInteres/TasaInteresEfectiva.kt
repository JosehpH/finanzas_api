package com.example.finanzasbackend.model.credito.tasaInteres

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import lombok.Data

@Entity
@DiscriminatorValue(value = "EFECTIVA")
@Data
class TasaInteresEfectiva(periodo: TipoPeriodo, tasa: Float) : TasaInteres(periodo, tasa){

}