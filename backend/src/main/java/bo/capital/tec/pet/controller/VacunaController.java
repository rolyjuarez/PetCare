package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.VacunaRequestDTO;
import bo.capital.tec.pet.dto.VacunaResponseDTO;
import bo.capital.tec.pet.dto.VacunaSummaryDTO;
import bo.capital.tec.pet.service.VacunaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vacunas")
@RequiredArgsConstructor
@Tag(name = "Vacunas", description = "API de gestion de vacunas")
public class VacunaController {

    private final VacunaService vacunaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear vacuna")
    public ResponseEntity<ApiResponse<VacunaResponseDTO>> create(@Valid @RequestBody VacunaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(vacunaService.create(dto), "Vacuna creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vacuna por ID")
    public ResponseEntity<ApiResponse<VacunaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(vacunaService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar vacunas")
    public ResponseEntity<ApiResponse<PagedResponse<VacunaSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(vacunaService.getAll(nombre, activo, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vacuna")
    public ResponseEntity<ApiResponse<VacunaResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody VacunaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(vacunaService.update(id, dto), "Vacuna actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vacuna")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vacunaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vacuna eliminada"));
    }
}
