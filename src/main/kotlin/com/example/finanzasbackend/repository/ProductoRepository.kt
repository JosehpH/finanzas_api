package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.Cliente
import com.example.finanzasbackend.model.Producto
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProductoRepository : JpaRepository<Producto,Long>{
    fun existsByNombre(nombre:String):Boolean
    fun findAllByActivoIsTrue():List<Producto>

    @Query("SELECT r  FROM Producto r WHERE " +
            "(lower(r.nombre) LIKE lower(concat('%',:keyword,'%') ) OR "+
            "lower(r.descripcion) LIKE lower(concat('%',:keyword,'%') ) )AND "+
            "r.activo = true "
    )
    fun findByKeyword(keyword:String):List<Producto>
}