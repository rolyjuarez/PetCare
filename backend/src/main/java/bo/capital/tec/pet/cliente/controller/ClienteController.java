package bo.capital.tec.pet.cliente.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.cliente.dto.ClienteRequestDTO;
import bo.capital.tec.pet.cliente.dto.ClienteResponseDTO;
import bo.capital.tec.pet.cliente.dto.ClienteSummaryDTO;
import bo.capital.tec.pet.cliente.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API de gestion de clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> create(@Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(clienteService.create(dto), "Cliente creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getById(id)));
    }

    @GetMapping("/by-persona/{personaId}")
    @Operation(summary = "Obtener cliente por persona")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> getByPersonaId(@PathVariable Long personaId) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getByPersonaId(personaId)));
    }

    @GetMapping
    @Operation(summary = "Listar clientes")
    public ResponseEntity<ApiResponse<PagedResponse<ClienteSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String ci,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.getAll(nombre, ci, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(clienteService.update(id, dto), "Cliente actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cliente eliminado"));
    }
}
