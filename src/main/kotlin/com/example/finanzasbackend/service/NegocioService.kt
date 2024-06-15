package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Negocio
import com.example.finanzasbackend.repository.NegocioRepository
import org.springframework.security.core.context.SecurityContextHolder
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
    fun getNegocio():Negocio?{
        val email:String = SecurityContextHolder.getContext().authentication.name
        return negocioRepository.findByEmail(email).get()
    }
    fun getAll():List<Negocio> = negocioRepository.findAll()
    fun updateNegocio(toModel: Negocio): Negocio? {
        val email:String = SecurityContextHolder.getContext().authentication.name
        val negocio = negocioRepository.findByEmail(email).get()
        negocio.nombre = toModel.nombre
        negocio.telefono = toModel.telefono
        negocio.ruc = toModel.ruc
        negocio.direccion  =toModel.direccion
        negocio.email = toModel.email

        negocioRepository.save(negocio);
        return negocio;
    }
}