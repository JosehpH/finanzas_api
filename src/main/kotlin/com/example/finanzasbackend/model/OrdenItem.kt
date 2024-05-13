package com.example.finanzasbackend.model

import jakarta.persistence.*

@Entity
class OrdenItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long = 0

    @ManyToOne
    var producto:Producto? = null

    var cantidad:Int = 0

    var precio:Float=0f

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orden_id")
    var orden:Orden?=null

    constructor(product: Producto, cantidad: Int) {
        this.producto = product
        this.cantidad = cantidad
        this.precio = product.precio
    }
    fun calcularSubtotal():Float{
        return precio * cantidad
    }
    fun addToOrden(orden:Orden){
        this.orden = orden
    }
}