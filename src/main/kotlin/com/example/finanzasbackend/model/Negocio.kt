package com.example.finanzasbackend.model

import com.example.finanzasbackend.model.credito.CreditoAnualidad
import com.example.finanzasbackend.model.credito.CreditoValoFuturo
import com.example.finanzasbackend.model.credito.EstadoCuota
import com.example.finanzasbackend.valueobjects.Role
import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Data
import java.util.UUID

@Entity(name = "negocios")
@AllArgsConstructor
@Data
class Negocio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0
    var nombre: String = ""
    var ruc: String = ""
    var telefono: String = ""
    var direccion: String = ""
    var email: String = ""
    var password: String = ""
    @Enumerated(EnumType.STRING)
    var role:Role = Role.USER

    @OneToMany(mappedBy = "negocio", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    var clientes:MutableList<Cliente> = mutableListOf()

    @OneToMany(mappedBy = "negocio",cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var productos:MutableList<Producto> = mutableListOf()

    constructor(nombre: String, ruc: String, telefono: String, direccion: String, email: String, password: String) {
        this.nombre = nombre
        this.ruc = ruc
        this.telefono = telefono
        this.direccion = direccion
        this.email = email
        this.password = password
    }

    fun registrarCliente(cliente:Cliente){
        clientes.add(cliente)
    }

    fun registrarProducto(producto:Producto){
        productos.add(producto)
    }

    fun getNumeroClientesActivos():Int{
        return clientes.size
    }
    fun getNumeroCreditosPagoPendiente():Int{
        var cant = 0
        for( cliente in clientes){
            if(cliente.cuenta!=null)
                for(credito in cliente.cuenta!!.creditos){
                    if(credito is CreditoValoFuturo)
                        cant+= if(credito.cuota!!.estadoCuota==EstadoCuota.PENDIENTE) 1 else 0
                    if(credito is CreditoAnualidad)
                        (credito as CreditoAnualidad).cuotas.forEach {it->
                            cant+= if(it.estadoCuota==EstadoCuota.PENDIENTE) 1 else 0
                        }
                }
        }
        return cant;
    }
    fun getTotalCreditoOtorgados():Int{
        var cant = 0;
        for( cliente in clientes){
            if(cliente.cuenta!=null)
                cant+=(cliente.cuenta!!.creditos.size)
        }
        return cant;
    }
    fun getNumeroCreditosPagoAtrasado():Int{
        var cant = 0
        for( cliente in clientes){
            if(cliente.cuenta!=null)
                for(credito in cliente.cuenta!!.creditos){
                    if(credito is CreditoValoFuturo)
                        cant+= if(credito.cuota!!.estadoCuota==EstadoCuota.ATRASADA) 1 else 0
                    if(credito is CreditoAnualidad)
                        (credito as CreditoAnualidad).cuotas.forEach {it->
                            cant+= if(it.estadoCuota==EstadoCuota.ATRASADA) 1 else 0
                        }
                }
        }
        return cant;
    }
}

