package bo.capital.tec.pet.modules.direccion.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.direccion.dto.DireccionRequestDTO;
import bo.capital.tec.pet.modules.direccion.dto.DireccionResponseDTO;
import bo.capital.tec.pet.modules.direccion.dto.DireccionSummaryDTO;
import bo.capital.tec.pet.modules.direccion.service.DireccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/direcciones")
@RequiredArgsConstructor
@Tag(name = "Direcciones", description = "API de gestion de direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear direccion")
    public ResponseEntity<ApiResponse<DireccionResponseDTO>> create(@Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(direccionService.create(dto), "Direccion creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener direccion por ID")
    public ResponseEntity<ApiResponse<DireccionResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(direccionService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar direcciones")
    public ResponseEntity<ApiResponse<PagedResponse<DireccionSummaryDTO>>> getAll(
            @RequestParam(required = false) String calle,
            @RequestParam(required = false) Long ciudadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(direccionService.getAll(calle, ciudadId, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar direccion")
    public ResponseEntity<ApiResponse<DireccionResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody DireccionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(direccionService.update(id, dto), "Direccion actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar direccion")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        direccionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Direccion eliminada"));
    }
}
