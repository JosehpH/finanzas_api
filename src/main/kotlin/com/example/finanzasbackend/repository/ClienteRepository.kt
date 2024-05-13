package com.example.finanzasbackend.repository;

import com.example.finanzasbackend.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository :JpaRepository<Cliente,Long>{

}
