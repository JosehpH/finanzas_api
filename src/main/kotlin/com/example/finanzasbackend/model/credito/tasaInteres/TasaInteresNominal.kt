package com.example.finanzasbackend.model.credito.tasaInteres

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import lombok.Data

@Entity
@DiscriminatorValue(value = "NOMINAL")
@Data
class TasaInteresNominal(
        @Enumerated(EnumType.STRING)
        val periodoCapitalizacion: TipoPeriodo?,
        periodo: TipoPeriodo,
        tasa: Float
) : TasaInteres(periodo, tasa) {

}