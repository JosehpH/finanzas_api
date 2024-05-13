package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.Cuenta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CuentaRepository :JpaRepository<Cuenta,Long>{
}