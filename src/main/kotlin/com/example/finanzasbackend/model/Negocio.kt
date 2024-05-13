package com.example.finanzasbackend.model

import com.example.finanzasbackend.valueobjects.Role
import jakarta.persistence.*
import lombok.AllArgsConstructor
import java.util.UUID

@Entity(name = "negocios")
@AllArgsConstructor
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

}

