package com.example.finanzasbackend.model.credito.gracia

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import lombok.Data

@Entity
@DiscriminatorValue(value = "TOTAL")
@Data
class PeriodoGraciaTotal(numCuotas: Int) : PeriodoGracia(numCuotas) {
}