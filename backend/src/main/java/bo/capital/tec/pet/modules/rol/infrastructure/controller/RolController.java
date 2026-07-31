package bo.capital.tec.pet.modules.rol.infrastructure.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.rol.dto.RolRequestDTO;
import bo.capital.tec.pet.modules.rol.dto.RolResponseDTO;
import bo.capital.tec.pet.modules.rol.dto.RolSummaryDTO;
import bo.capital.tec.pet.modules.rol.application.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "API de gestion de roles")
public class RolController {

    private final RolService rolService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear rol")
    public ResponseEntity<ApiResponse<RolResponseDTO>> create(@Valid @RequestBody RolRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(rolService.create(dto), "Rol creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<ApiResponse<RolResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(rolService.getById(id)));
    }

    @GetMapping("/by-name/{nombre}")
    @Operation(summary = "Obtener rol por nombre")
    public ResponseEntity<ApiResponse<RolResponseDTO>> getByName(@PathVariable String nombre) {
        return ResponseEntity.ok(ApiResponse.success(rolService.getByName(nombre)));
    }

    @GetMapping
    @Operation(summary = "Listar roles")
    public ResponseEntity<ApiResponse<PagedResponse<RolSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(rolService.getAll(nombre, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol")
    public ResponseEntity<ApiResponse<RolResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody RolRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(rolService.update(id, dto), "Rol actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        rolService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Rol eliminado"));
    }

    @GetMapping("/{id}/menus")
    @Operation(summary = "Obtener menus del rol")
    public ResponseEntity<ApiResponse<List<Long>>> getMenuIdsByRolId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(rolService.getMenuIdsByRolId(id)));
    }
}
