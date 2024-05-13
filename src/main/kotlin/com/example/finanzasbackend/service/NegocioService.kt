package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.repository.NegocioRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class NegocioService(private val negocioRepository: NegocioRepository) {
    fun registrarNegocio(negocio:Negocio):Negocio?{
        val existe = negocioRepository.existsByEmailOrRuc(negocio.email,negocio.ruc)
        if(existe)
            return null
        negocioRepository.save(negocio)
        return negocio
    }
    fun getAll():List<Negocio> = negocioRepository.findAll()
}