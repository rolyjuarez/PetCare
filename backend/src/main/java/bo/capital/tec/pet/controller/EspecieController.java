package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.EspecieRequestDTO;
import bo.capital.tec.pet.dto.EspecieResponseDTO;
import bo.capital.tec.pet.dto.EspecieSummaryDTO;
import bo.capital.tec.pet.service.EspecieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/especies")
@RequiredArgsConstructor
@Tag(name = "Especies", description = "API de gestion de especies")
public class EspecieController {

    private final EspecieService especieService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear especie")
    public ResponseEntity<ApiResponse<EspecieResponseDTO>> create(@Valid @RequestBody EspecieRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(especieService.create(dto), "Especie creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener especie por ID")
    public ResponseEntity<ApiResponse<EspecieResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(especieService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar especies")
    public ResponseEntity<ApiResponse<PagedResponse<EspecieSummaryDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(especieService.getAll(page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar especie")
    public ResponseEntity<ApiResponse<EspecieResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody EspecieRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(especieService.update(id, dto), "Especie actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar especie")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        especieService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Especie eliminada"));
    }
}
