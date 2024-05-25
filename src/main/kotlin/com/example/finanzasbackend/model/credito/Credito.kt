package com.example.finanzasbackend.model.credito
import com.example.finanzasbackend.model.Cuenta
import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteres
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresEfectiva
import com.example.finanzasbackend.model.credito.tasaInteres.TasaInteresNominal
import com.example.finanzasbackend.model.credito.tasaInteres.TipoPeriodo
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data
import java.time.LocalDate
import java.util.Date
import kotlin.math.roundToInt

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "tipo_credito",
        discriminatorType = DiscriminatorType.STRING
)
abstract class Credito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @OneToOne( mappedBy ="credito",fetch = FetchType.EAGER,cascade = [CascadeType.ALL], orphanRemoval = true)
    var consumo:Orden? = null

    var saldo:Float = 0F

    @OneToOne(cascade = [CascadeType.ALL])
    var tasaCompensatoria: TasaInteres = TasaInteresEfectiva(TipoPeriodo.DIARIO,5f)

    @OneToOne(cascade = [CascadeType.ALL])
    var tasaMoratoria: TasaInteres = TasaInteresNominal(TipoPeriodo.MENSUAL,TipoPeriodo.MENSUAL,6f)

    var pagoInicial:Float = 0F

    var fechaDesembolso:LocalDate = LocalDate.now()

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name= "cuenta_id")
    var cuenta:Cuenta? = null

    constructor(){ }
    constructor(consumo:Orden,tasaCompensatoria: TasaInteres, tasaMoratoria: TasaInteres, pagoInicial: Float, fechaDesembolso: LocalDate) {
        this.consumo = consumo
        this.saldo = consumo.calcularTotal()
        this.tasaCompensatoria = tasaCompensatoria
        this.tasaMoratoria = tasaMoratoria
        this.pagoInicial = pagoInicial
        this.fechaDesembolso = fechaDesembolso
    }
    abstract fun calcularCuotas()
    fun asignarToCuenta(cuenta:Cuenta){
        this.cuenta = cuenta
    }
     fun roundTo2Decimals(n:Float):Float{
        return ((n*100.0).roundToInt()/100.0).toFloat()
    }
}