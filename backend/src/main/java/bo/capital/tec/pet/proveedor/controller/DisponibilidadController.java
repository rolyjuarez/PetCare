package bo.capital.tec.pet.proveedor.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.proveedor.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.proveedor.dto.DisponibilidadResponseDTO;
import bo.capital.tec.pet.proveedor.service.DisponibilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disponibilidades")
@RequiredArgsConstructor
@Tag(name = "Disponibilidades", description = "API de gestion de disponibilidades")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear disponibilidad")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> create(@Valid @RequestBody DisponibilidadRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(disponibilidadService.create(dto), "Disponibilidad creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener disponibilidad por ID")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar disponibilidades")
    public ResponseEntity<ApiResponse<PagedResponse<DisponibilidadResponseDTO>>> getAll(
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.getAll(proveedorId, servicioId, page, size)));
    }

    @GetMapping("/by-proveedor-servicio/{proveedorId}/{servicioId}")
    @Operation(summary = "Obtener disponibilidades por proveedor y servicio")
    public ResponseEntity<ApiResponse<List<DisponibilidadResponseDTO>>> getByProveedorAndServicio(
            @PathVariable Long proveedorId, @PathVariable Long servicioId) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.getByProveedorAndServicio(proveedorId, servicioId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar disponibilidad")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody DisponibilidadRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(disponibilidadService.update(id, dto), "Disponibilidad actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar disponibilidad")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        disponibilidadService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Disponibilidad eliminada"));
    }
}
