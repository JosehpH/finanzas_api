package com.example.finanzasbackend.controller.Order

import com.example.finanzasbackend.dto.orden.OrdenRequest
import com.example.finanzasbackend.dto.orden.OrdenResponse
import com.example.finanzasbackend.dto.orden.OrderItemResponse
import com.example.finanzasbackend.dto.producto.ProductoResponse
import com.example.finanzasbackend.model.Orden
import com.example.finanzasbackend.model.OrdenItem
import com.example.finanzasbackend.model.Producto
import com.example.finanzasbackend.service.OrdenService
import com.example.finanzasbackend.service.ProductoService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/ordenes")
@Tag(name = "Ordenes", description = "Controlador para procesar las ordenes de los clientes")
class OrderController(
        private val productoService: ProductoService,
        private val ordenService: OrdenService
) {

    @Operation(summary = "Crea tu orden enviando los orderItems")
    @PostMapping()
    fun crearOrden(@RequestBody request: OrdenRequest): ResponseEntity<OrdenResponse> {
        val orden = ordenService.crearOrden(request.toEntity())
        return ResponseEntity(orden.toResponse(), HttpStatus.CREATED)
    }

    @Operation(summary = "Obtén las ordenes realizadas para los créditos por los clientes")
    @GetMapping("/{clienteId}/clientes")
    fun getOrdenesByClienteId(@PathVariable clienteId: Long): ResponseEntity<List<OrdenResponse>> {
        val ordenesResponse: List<OrdenResponse> = ordenService.getOrdenesByClienteId(clienteId).map {
            it.toResponse()
        }
        return ResponseEntity(ordenesResponse,HttpStatus.OK)
    }

    @Operation(summary = "Obtén todos los consumos realizados por el negocio")
    @GetMapping("/negocio")
    fun getOrdenesByNegocio(): ResponseEntity<List<OrdenResponse>> {
        val ordenesResponse: List<OrdenResponse> = ordenService.getOrdenesByNegocio().map {
            it.toResponse()
        }
        return ResponseEntity(ordenesResponse,HttpStatus.OK)
    }

    @Operation(summary = "Obtén la orden realizada para un crédito ingresando el id del crédito")
    @GetMapping("/{creditoId}")
    fun getOrdenByCreditoId(@PathVariable creditoId:Long):ResponseEntity<OrdenResponse>{
        val orden = ordenService.getOrdenByCreditoId(creditoId)
        return ResponseEntity(orden.toResponse(),HttpStatus.OK)
    }

    private fun OrdenRequest.toEntity(): Orden {
        val ordenItems: List<OrdenItem> = this.items.map {
            val producto: Producto = productoService.getProductoById(it.productId)
            OrdenItem(
                    product = producto,
                    cantidad = it.cantidad
            )
        }
        return Orden(
                items = ordenItems.toMutableList()
        )
    }
    private fun Orden.toResponse(): OrdenResponse {
        val ordenItems: List<OrderItemResponse> = this.items.map {
            OrderItemResponse(
                    id = it.id,
                    producto = ProductoResponse(
                            id = it.producto!!.id,
                            nombre = it.producto!!.nombre,
                            descripcion = it.producto!!.descripcion,
                            imagenes = it.producto!!.imagenes,
                            precio = it.precio
                    ),
                    cantidad = it.cantidad,
                    subTotal = it.calcularSubtotal()
            )
        }
        return OrdenResponse(
                id = this.id,
                items = ordenItems,
                total = this.calcularTotal()
        )
    }
}