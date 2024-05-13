package com.example.finanzasbackend.model

import com.example.finanzasbackend.model.credito.Credito
import jakarta.persistence.*

@Entity
class Orden {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @OneToMany(mappedBy = "orden",cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var items:MutableList<OrdenItem> = mutableListOf()

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="credito_id")
    var credito:Credito? = null

    constructor(items: MutableList<OrdenItem>) {
        this.items = items
    }


    fun calcularTotal():Float{
        var total:Float = 0f
        for(item in items){
            total += item.calcularSubtotal()
        }
        return total
    }

    fun asignarToCredito(credito:Credito){
        this.credito = credito
    }
}