package bo.capital.tec.pet.modules.direccion.infrastructure.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.direccion.dto.CiudadRequestDTO;
import bo.capital.tec.pet.modules.direccion.dto.CiudadResponseDTO;
import bo.capital.tec.pet.modules.direccion.dto.CiudadSummaryDTO;
import bo.capital.tec.pet.modules.direccion.application.service.CiudadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ciudades")
@RequiredArgsConstructor
@Tag(name = "Ciudades", description = "API de gestion de ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear ciudad")
    public ResponseEntity<ApiResponse<CiudadResponseDTO>> create(@Valid @RequestBody CiudadRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ciudadService.create(dto), "Ciudad creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ciudad por ID")
    public ResponseEntity<ApiResponse<CiudadResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(ciudadService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar ciudades")
    public ResponseEntity<ApiResponse<PagedResponse<CiudadSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(ciudadService.getAll(nombre, departamentoId, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ciudad")
    public ResponseEntity<ApiResponse<CiudadResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody CiudadRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(ciudadService.update(id, dto), "Ciudad actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ciudad")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        ciudadService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Ciudad eliminada"));
    }
}
