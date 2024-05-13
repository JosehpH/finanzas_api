package com.example.finanzasbackend.controller.producto

import com.example.finanzasbackend.dto.producto.ProductoRequest
import com.example.finanzasbackend.dto.producto.ProductoResponse
import com.example.finanzasbackend.model.Producto
import com.example.finanzasbackend.service.ProductoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/products")
@Tag(name = "Productos", description = "Controlador para los productos de cada negocio")
class ProductoController(
        private val productoService: ProductoService
) {

    @Operation(summary = "Obtén todos los productos registrados del negocio")
    @GetMapping
    fun getAllProductsByNegocio(): ResponseEntity<List<ProductoResponse>> {
        val response:List<ProductoResponse> = productoService.getAllProductosByNegocio().map { it.toResponse() }
        return ResponseEntity(response,HttpStatus.OK)
    }

    @Operation(summary="Registra un nuevo producto para el negocio")
    @PostMapping
    fun createProduct(@RequestBody request: ProductoRequest): ResponseEntity<ProductoResponse> {
        val producto = productoService.registrarProducto(request.toEntity())
        return ResponseEntity(producto.toResponse(),HttpStatus.CREATED)
    }

    @Operation(summary="Actualiza los datos de algún producto del negocio")
    @PutMapping("/{productId}")
    fun updateProduct(@PathVariable productId:Long ,@RequestBody request: ProductoRequest): ResponseEntity<ProductoResponse> {
        val producto = productoService.actualizarProducto(request.toEntity(),productId)
        return ResponseEntity(producto.toResponse(),HttpStatus.OK)
    }

    @Operation(summary = "Elimina algún producto de tu negocio")
    @DeleteMapping("/{productId}")
    fun deleteProduct(@PathVariable productId:Long): ResponseEntity<Unit> {
        productoService.eliminarProducto(productId)
        return ResponseEntity(null,HttpStatus.OK)
    }

    private fun ProductoRequest.toEntity(): Producto = Producto(
            nombre = this.nombre,
            descripcion = this.descripcion,
            imagenes = this.imagenes,
            precio = this.precio
    )

    private fun Producto.toResponse(): ProductoResponse = ProductoResponse(
            id = this.id,
            nombre = this.nombre,
            descripcion = this.descripcion,
            imagenes = this.imagenes,
            precio = this.precio
    )
}