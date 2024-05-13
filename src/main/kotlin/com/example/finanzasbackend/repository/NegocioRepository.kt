package com.example.finanzasbackend.repository;

import com.example.finanzasbackend.model.Negocio
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository;
import java.util.Optional

@Repository
interface NegocioRepository : JpaRepository<Negocio,Long >{
    fun findByEmail(email:String):Optional<Negocio>
    fun findByNombre(nombre:String):Optional<Negocio>
    fun existsByEmailOrRuc(email:String,ruc:String):Boolean
}
