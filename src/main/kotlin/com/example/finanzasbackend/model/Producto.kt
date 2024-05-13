package com.example.finanzasbackend.model

import jakarta.persistence.*

@Entity
class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    var nombre: String = ""

    var descripcion: String= ""

    @ElementCollection
    var imagenes: List<String> = mutableListOf()

    var precio: Float = 0f

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name="negocio_id")
    var negocio:Negocio?=null

    constructor(nombre: String, descripcion: String, imagenes: List<String>, precio: Float) {
        this.nombre = nombre
        this.descripcion = descripcion
        this.imagenes = imagenes
        this.precio = precio
    }


    fun actualizarValores(producto:Producto){
        this.nombre = producto.nombre
        this.descripcion = producto.descripcion
        this.imagenes = producto.imagenes
        this.precio = producto.precio
    }
}