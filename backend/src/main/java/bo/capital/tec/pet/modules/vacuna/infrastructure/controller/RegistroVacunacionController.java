package bo.capital.tec.pet.modules.vacuna.infrastructure.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionSummaryDTO;
import bo.capital.tec.pet.modules.vacuna.application.service.RegistroVacunacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/registros-vacunacion")
@RequiredArgsConstructor
@Tag(name = "Registros de Vacunacion", description = "API de gestion de registros de vacunacion")
public class RegistroVacunacionController {

    private final RegistroVacunacionService registroVacunacionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear registro de vacunacion")
    public ResponseEntity<ApiResponse<RegistroVacunacionResponseDTO>> create(@Valid @RequestBody RegistroVacunacionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(registroVacunacionService.create(dto), "Registro de vacunacion creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de vacunacion por ID")
    public ResponseEntity<ApiResponse<RegistroVacunacionResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(registroVacunacionService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar registros de vacunacion")
    public ResponseEntity<ApiResponse<PagedResponse<RegistroVacunacionSummaryDTO>>> getAll(
            @RequestParam(required = false) Long mascotaId,
            @RequestParam(required = false) Long vacunaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                registroVacunacionService.getAll(mascotaId, vacunaId, fechaDesde, fechaHasta, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de vacunacion")
    public ResponseEntity<ApiResponse<RegistroVacunacionResponseDTO>> update(@PathVariable Long id,
            @Valid @RequestBody RegistroVacunacionRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(registroVacunacionService.update(id, dto), "Registro actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de vacunacion")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        registroVacunacionService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Registro eliminado"));
    }
}
