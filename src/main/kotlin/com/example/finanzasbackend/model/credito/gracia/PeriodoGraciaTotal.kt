package com.example.finanzasbackend.model.credito.gracia

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue(value = "TOTAL")
class PeriodoGraciaTotal(numCuotas: Int) : PeriodoGracia(numCuotas) {
}