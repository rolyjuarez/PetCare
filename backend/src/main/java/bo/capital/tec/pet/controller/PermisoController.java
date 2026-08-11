package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.PermisoRequestDTO;
import bo.capital.tec.pet.dto.PermisoResponseDTO;
import bo.capital.tec.pet.service.PermisoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permisos")
@RequiredArgsConstructor
@Tag(name = "Permisos", description = "API de gestion de permisos")
public class PermisoController {

    private final PermisoService permisoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear permiso")
    public ResponseEntity<ApiResponse<PermisoResponseDTO>> create(@Valid @RequestBody PermisoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(permisoService.create(dto), "Permiso creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener permiso por ID")
    public ResponseEntity<ApiResponse<PermisoResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(permisoService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar permisos")
    public ResponseEntity<ApiResponse<PagedResponse<PermisoResponseDTO>>> getAll(
            @RequestParam(required = false) Long rolId,
            @RequestParam(required = false) Long menuId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(permisoService.getAll(rolId, menuId, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar permiso")
    public ResponseEntity<ApiResponse<PermisoResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody PermisoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(permisoService.update(id, dto), "Permiso actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar permiso")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        permisoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Permiso eliminado"));
    }

    @GetMapping("/by-rol/{rolId}")
    @Operation(summary = "Obtener permisos por rol")
    public ResponseEntity<ApiResponse<List<PermisoResponseDTO>>> getByRolId(@PathVariable Long rolId) {
        return ResponseEntity.ok(ApiResponse.success(permisoService.getByRolId(rolId)));
    }
}
