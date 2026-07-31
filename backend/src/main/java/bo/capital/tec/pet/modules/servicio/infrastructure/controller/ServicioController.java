package bo.capital.tec.pet.modules.servicio.infrastructure.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.servicio.dto.ServicioRequestDTO;
import bo.capital.tec.pet.modules.servicio.dto.ServicioResponseDTO;
import bo.capital.tec.pet.modules.servicio.dto.ServicioSummaryDTO;
import bo.capital.tec.pet.modules.servicio.application.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
@Tag(name = "Servicios", description = "API de gestion de servicios")
public class ServicioController {

    private final ServicioService servicioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear servicio")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> create(@Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(servicioService.create(dto), "Servicio creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener servicio por ID")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar servicios")
    public ResponseEntity<ApiResponse<PagedResponse<ServicioSummaryDTO>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "true") boolean activo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.getAll(nombre, categoriaId, activo, page, size)));
    }

    @GetMapping("/active")
    @Operation(summary = "Listar servicios activos")
    public ResponseEntity<ApiResponse<List<ServicioSummaryDTO>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(servicioService.getActive()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar servicio")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(servicioService.update(id, dto), "Servicio actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar servicio")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        servicioService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Servicio eliminado"));
    }
}
