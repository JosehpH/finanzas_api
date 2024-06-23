package com.example.finanzasbackend.model

import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.EstadoCuota
import jakarta.persistence.*
import lombok.Data

@Entity(name = "clientes")
@Data
class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long=0
    var nombres:String=""
    var apellidoPaterno:String=""
    var apellidoMaterno:String=""
    var dni:String = ""
    var email:String=""
    var telefono:String=""
    var photo:String? = null

    @OneToOne(mappedBy = "cliente",cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var cuenta:Cuenta? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="negocio_id")
    var negocio:Negocio? = null

    constructor(nombres: String, apellidoPaterno: String, apellidoMaterno: String, dni: String, email: String, telefono: String, photo: String?=null) {
        this.nombres = nombres
        this.apellidoPaterno = apellidoPaterno
        this.apellidoMaterno = apellidoMaterno
        this.dni = dni
        this.email = email
        this.telefono = telefono
        this.photo = photo
    }
    fun aperturarCuenta(nuevaCuenta:Cuenta){
        if(cuenta==null) {
            cuenta = nuevaCuenta
            this.cuenta!!.asignarToCliente(this)
        }
            else throw IllegalArgumentException("No se puede aperturar una nueva cuenta porque ya existe una")
    }

    fun esApto():Boolean{
        if(this.cuenta==null) return false
        val creditos = this.cuenta!!.creditos
        for (credito in creditos){
            when(credito){
                is CreditoValoFuturo -> {
                    if(credito.cuota!!.estadoCuota==EstadoCuota.ATRASADA || credito.cuota!!.estadoCuota==EstadoCuota.PENDIENTE )
                        return false
                }
                is CreditoAnualidad ->{
                    for(cuota in credito.cuotas){
                        if(cuota.estadoCuota==EstadoCuota.ATRASADA || cuota.estadoCuota==EstadoCuota.PENDIENTE)
                            return false
                    }
                }
            }
        }
        return true
    }

}