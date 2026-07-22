package bo.capital.tec.pet.sucursal.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.sucursal.dto.SucursalRequestDTO;
import bo.capital.tec.pet.sucursal.dto.SucursalResponseDTO;
import bo.capital.tec.pet.sucursal.dto.SucursalSummaryDTO;
import bo.capital.tec.pet.sucursal.service.SucursalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "API de gestion de sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear sucursal")
    public ResponseEntity<ApiResponse<SucursalResponseDTO>> create(@Valid @RequestBody SucursalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(sucursalService.create(dto), "Sucursal creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal por ID")
    public ResponseEntity<ApiResponse<SucursalResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(sucursalService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar sucursales")
    public ResponseEntity<ApiResponse<PagedResponse<SucursalSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(sucursalService.getAll(nombre, activo, page, size)));
    }

    @GetMapping("/active")
    @Operation(summary = "Listar sucursales activas")
    public ResponseEntity<ApiResponse<List<SucursalSummaryDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(sucursalService.getActive()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sucursal")
    public ResponseEntity<ApiResponse<SucursalResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody SucursalRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(sucursalService.update(id, dto), "Sucursal actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sucursal")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        sucursalService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Sucursal eliminada"));
    }
}
