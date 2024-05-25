package com.example.finanzasbackend.model.credito.gracia

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import lombok.Data

@Entity
@DiscriminatorValue(value = "PARCIAL")
@Data
class PeriodoGraciaParcial(numCuotas: Int) : PeriodoGracia(numCuotas) {

}