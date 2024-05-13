package com.example.finanzasbackend.controller.clientes;

import com.example.finanzasbackend.dto.clientes.ClienteRequest
import com.example.finanzasbackend.dto.clientes.ClienteResponse
import com.example.finanzasbackend.dto.cuenta.AperturarCuentaRequest
import com.example.finanzasbackend.dto.cuenta.AperturarCuentaResponse
import com.example.finanzasbackend.dto.cuenta.CuentaResponse
import com.example.finanzasbackend.model.Cliente
import com.example.finanzasbackend.model.Cuenta
import com.example.finanzasbackend.service.ClienteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Cliente", description = "Controller para las operaciones relacionadas con los clientes")
class ClienteController(
        private val clienteService: ClienteService
) {

    @Operation(summary = "Obtiene la lista de clientes de tu negocio")
    @GetMapping("/negocio")
    fun getAllByNegocio(): ResponseEntity<List<ClienteResponse>> {
        return ResponseEntity(clienteService.getAllByNegocio().map { it.toResponse() }, HttpStatus.OK)
    }

    @Operation(summary = "Obtiene la lista de todos los clientes (solo para ADMINS)")
    @GetMapping
    fun getAll(): ResponseEntity<List<ClienteResponse>> {
        return ResponseEntity(clienteService.getAll().map { it.toResponse() }, HttpStatus.OK)
    }

    @Operation(summary = "Registra un cliente a tu negocio")
    @PostMapping
    fun registrarCliente(@RequestBody request:ClienteRequest):ResponseEntity<ClienteResponse>{
        val cliente = clienteService.registrarCliente(request.toEntity())
        return ResponseEntity(cliente?.toResponse(),HttpStatus.CREATED)
    }
    @Operation(summary = "Apertura la cuenta de créditos del cliente con un límite financiero")
    @PostMapping("/{clienteId}/aperturar-cuenta")
    fun aperturarCuenta(@PathVariable clienteId:Long, @RequestBody request: AperturarCuentaRequest):ResponseEntity<AperturarCuentaResponse>{
        val cliente = clienteService.aperturarCuenta(clienteId, cuenta = request.toEntity())
        return ResponseEntity(cliente.toAperturarCuentaResponse(),HttpStatus.OK)
    }

    private fun Cliente.toResponse() =
            ClienteResponse(
                    id = this.id,
                    nombres = this.nombres,
                    apellidoPaterno = this.apellidoPaterno,
                    apellidoMaterno = this.apellidoMaterno,
                    dni = this.dni,
                    email = this.email,
                    telefono = this.telefono,
                    photo = this.photo
            )
    private fun ClienteRequest.toEntity():Cliente=
            Cliente(
                    nombres = this.nombres,
                    apellidoPaterno=this.apellidoPaterno,
                    apellidoMaterno = this.apellidoMaterno,
                    dni = this.dni,
                    email = this.email,
                    telefono = this.telefono,
                    photo = this.photo
            )
    private fun AperturarCuentaRequest.toEntity():Cuenta=
            Cuenta(
                    lineaCredito = this.limiteCrediticio
            )
    private fun Cliente.toAperturarCuentaResponse():AperturarCuentaResponse =
            AperturarCuentaResponse(
                    id = this.id,
                    nombres = this.nombres,
                    apellidoPaterno = this.apellidoPaterno,
                    apellidoMaterno = this.apellidoMaterno,
                    dni = this.dni,
                    email = this.email,
                    telefono = this.telefono,
                    photo = this.photo,
                    cuenta = CuentaResponse(
                            id=this.cuenta!!.id,
                            limiteCrediticio = this.cuenta!!.lineaCredito
                    )
            )
}
