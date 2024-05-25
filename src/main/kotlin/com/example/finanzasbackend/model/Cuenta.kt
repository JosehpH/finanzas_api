package com.example.finanzasbackend.model

import com.example.finanzasbackend.model.credito.Credito
import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.EstadoCuota
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import lombok.Data

@Entity(name = "cuentas_credito")
@Data
class Cuenta(var lineaCredito: Float) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @OneToMany(mappedBy = "cuenta",fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var creditos:MutableList<Credito> = mutableListOf()

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id")
    var cliente:Cliente? = null

    fun agregarCredito(credito:Credito){
        creditos.add(credito)
        credito.asignarToCuenta(this)
    }
    fun recuperarLineaCredito(amortizacion:Float){
        this.lineaCredito+=amortizacion;
    }
    fun reducirLineaCredito(montoCredito: Float){
        this.lineaCredito-=montoCredito;
    }
    fun actualizarLineaCredito(lineaCredito: Float){
        this.lineaCredito = lineaCredito;
    }
    fun hayPagosAtrasados():Boolean{
        for(credito in creditos){
            if(credito is CreditoValoFuturo && credito.cuota!!.estadoCuota == EstadoCuota.ATRASADA)
                return true
            else if (credito is CreditoAnualidad){
                for(cuota in (credito as CreditoAnualidad).cuotas){
                    if(cuota.estadoCuota == EstadoCuota.ATRASADA)
                        return true
                }
            }
        }
        return false
    }
    fun asignarToCliente(cliente:Cliente){
        this.cliente = cliente
    }
}