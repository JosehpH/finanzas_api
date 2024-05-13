package com.example.finanzasbackend.repository

import com.example.finanzasbackend.model.credito.Credito
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CreditoRepository :JpaRepository<Credito,Long>{
}