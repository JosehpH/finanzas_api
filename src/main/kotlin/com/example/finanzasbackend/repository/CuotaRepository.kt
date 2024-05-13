package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.credito.Cuota
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CuotaRepository :JpaRepository<Cuota,Long>{
}