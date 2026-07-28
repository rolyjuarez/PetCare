package bo.capital.tec.pet.modules.promocion.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/promociones")
@RequiredArgsConstructor
@Tag(name = "Promociones", description = "API de gestion de promociones")
public class PromocionController {

    private final PromocionService promocionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear promocion")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> create(@Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(promocionService.create(dto), "Promocion creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener promocion por ID")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar promociones")
    public ResponseEntity<ApiResponse<PagedResponse<PromocionSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.getAll(nombre, activo, page, size)));
    }

    @GetMapping("/active")
    @Operation(summary = "Listar promociones activas")
    public ResponseEntity<ApiResponse<List<PromocionSummaryDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(promocionService.getActive()));
    }

    @GetMapping("/active-by-date")
    @Operation(summary = "Listar promociones activas por fecha")
    public ResponseEntity<ApiResponse<List<PromocionSummaryDTO>>> getActiveByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.getActiveByDate(fecha)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promocion")
    public ResponseEntity<ApiResponse<PromocionResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(promocionService.update(id, dto), "Promocion actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promocion")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        promocionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Promocion eliminada"));
    }
}
