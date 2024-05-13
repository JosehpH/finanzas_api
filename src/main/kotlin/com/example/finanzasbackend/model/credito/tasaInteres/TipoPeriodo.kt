package com.example.finanzasbackend.model.credito.tasaInteres

enum class TipoPeriodo(val dias:Int) {
    DIARIO(1),
    QUINCENAL(15),
    MENSUAL(30),
    BIMESTRAL(60),
    TRIMESTRAL(90),
    CUATRIMESTRAL(120),
    SEMESTRAL(180),
    ANUAL(360)
}
