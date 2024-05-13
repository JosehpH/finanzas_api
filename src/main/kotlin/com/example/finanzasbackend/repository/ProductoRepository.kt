package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.Producto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductoRepository : JpaRepository<Producto,Long>{
    fun existsByNombre(nombre:String):Boolean
}