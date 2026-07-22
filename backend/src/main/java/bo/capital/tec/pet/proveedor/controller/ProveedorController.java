package bo.capital.tec.pet.proveedor.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorSummaryDTO;
import bo.capital.tec.pet.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "API de gestion de proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> create(@Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(proveedorService.create(dto), "Proveedor creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar proveedores")
    public ResponseEntity<ApiResponse<PagedResponse<ProveedorSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long especialidadId,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.getAll(nombre, especialidadId, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ProveedorRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.update(id, dto), "Proveedor actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        proveedorService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Proveedor eliminado"));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Buscar proveedores cercanos")
    public ResponseEntity<ApiResponse<List<ProveedorSummaryDTO>>> searchNearby(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam(defaultValue = "10") BigDecimal radioKm) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.searchNearby(lat, lng, radioKm)));
    }

    @GetMapping("/by-servicio/{servicioId}")
    @Operation(summary = "Obtener proveedores por servicio")
    public ResponseEntity<ApiResponse<List<ProveedorSummaryDTO>>> getByServicioId(@PathVariable Long servicioId) {
        return ResponseEntity.ok(ApiResponse.success(proveedorService.getByServicioId(servicioId)));
    }
}
