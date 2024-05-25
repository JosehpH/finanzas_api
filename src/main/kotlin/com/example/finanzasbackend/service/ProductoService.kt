package com.example.finanzasbackend.service

import com.example.finanzasbackend.model.Producto
import com.example.finanzasbackend.repository.NegocioRepository
import com.example.finanzasbackend.repository.ProductoRepository
import org.apache.coyote.BadRequestException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ProductoService(
        private val productoRepository: ProductoRepository,
        private val negocioRepository: NegocioRepository
) {
    fun getAllProductosByNegocio():List<Producto> = productoRepository.findAllByActivoIsTrue()

    fun getProductoById(productoId:Long):Producto{
        val found = productoRepository.findById(productoId);
        if(found.isEmpty)
            throw BadRequestException("El producto no existe")
        return found.get()
    }

    fun registrarProducto(producto:Producto):Producto{
        val found = productoRepository.existsByNombre(producto.nombre)
        if(found) throw BadRequestException("Ya existe un producto con el mismo nombre")
        val negocioEmail = SecurityContextHolder.getContext().authentication.name
        val negocio = negocioRepository.findByEmail(negocioEmail).get()

        producto.negocio = negocio
        negocio.registrarProducto(producto)
        negocioRepository.save(negocio)
        return producto
    }

    fun actualizarProducto(producto:Producto, productoId:Long):Producto{
        val found = productoRepository.findById(productoId)
        if(found.isEmpty) throw BadRequestException("El producto con el id asociado no se ha encontrado")
        val productoUpdated = found.get()

        productoUpdated.actualizarValores(producto = producto)
        return productoRepository.save(productoUpdated)
    }
    fun eliminarProducto(productoId:Long){
        val found = productoRepository.existsById(productoId)
        if(!found) throw BadRequestException("El producto con el id asociado no se ha encontrado")

        val product = productoRepository.findById(productoId).get()
        product.activo=false
        productoRepository.save(product)
    }

    fun findByKeyword(keyword:String) = productoRepository.findByKeyword(keyword)


}