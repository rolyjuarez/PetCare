package bo.capital.tec.pet.mascota.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.mascota.dto.RazaRequestDTO;
import bo.capital.tec.pet.mascota.dto.RazaResponseDTO;
import bo.capital.tec.pet.mascota.dto.RazaSummaryDTO;
import bo.capital.tec.pet.mascota.service.RazaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/razas")
@RequiredArgsConstructor
@Tag(name = "Razas", description = "API de gestion de razas")
public class RazaController {

    private final RazaService razaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear raza")
    public ResponseEntity<ApiResponse<RazaResponseDTO>> create(@Valid @RequestBody RazaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(razaService.create(dto), "Raza creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener raza por ID")
    public ResponseEntity<ApiResponse<RazaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(razaService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar razas")
    public ResponseEntity<ApiResponse<PagedResponse<RazaSummaryDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(razaService.getAll(page, size)));
    }

    @GetMapping("/by-especie/{especieId}")
    @Operation(summary = "Obtener razas por especie")
    public ResponseEntity<ApiResponse<List<RazaSummaryDTO>>> getByEspecieId(@PathVariable Long especieId) {
        return ResponseEntity.ok(ApiResponse.success(razaService.getByEspecieId(especieId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar raza")
    public ResponseEntity<ApiResponse<RazaResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody RazaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(razaService.update(id, dto), "Raza actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar raza")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        razaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Raza eliminada"));
    }
}
