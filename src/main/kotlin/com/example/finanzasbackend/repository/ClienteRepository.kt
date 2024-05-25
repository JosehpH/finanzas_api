package com.example.finanzasbackend.repository;

import com.example.finanzasbackend.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ClienteRepository :JpaRepository<Cliente,Long>{
    @Query("SELECT r  FROM clientes r WHERE " +
            "lower(r.nombres) LIKE lower(concat('%',:keyword,'%') ) OR "+
            "lower(r.apellidoPaterno) LIKE lower(concat('%',:keyword,'%') ) OR "+
            "lower(r.apellidoMaterno) LIKE lower(concat('%',:keyword,'%') ) OR "+
            "lower(r.dni) LIKE lower(concat(:keyword,'%') ) OR "+
            "lower(r.email) LIKE lower(concat('%',:keyword,'%') ) OR "+
            "lower(r.telefono) LIKE lower(concat('%',:keyword,'%') ) "
    )
    fun findByKeyword(keyword:String):List<Cliente>
    fun findByDni(dni:String): Optional<Cliente>
}
