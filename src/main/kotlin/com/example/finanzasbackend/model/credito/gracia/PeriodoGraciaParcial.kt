package com.example.finanzasbackend.model.credito.gracia

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue(value = "PARCIAL")
class PeriodoGraciaParcial(numCuotas: Int) : PeriodoGracia(numCuotas) {

}