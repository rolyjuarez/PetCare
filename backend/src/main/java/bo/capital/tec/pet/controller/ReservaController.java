package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.ReservaRequestDTO;
import bo.capital.tec.pet.dto.ReservaResponseDTO;
import bo.capital.tec.pet.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.service.ReservaService;
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
@RequestMapping("/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "API de gestion de reservas")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> create(@Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservaService.create(dto), "Reserva creada"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reserva por ID")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar reservas")
    public ResponseEntity<ApiResponse<PagedResponse<ReservaSummaryDTO>>> getAll(
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long mascotaId,
            @RequestParam(required = false) Long servicioId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaService.getAll(clienteId, mascotaId, servicioId, estado, fechaDesde, fechaHasta, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.update(id, dto), "Reserva actualizada"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reserva")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reserva eliminada"));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.confirmar(id), "Reserva confirmada"));
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> rechazar(@PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.rechazar(id, motivo), "Reserva rechazada"));
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.iniciar(id), "Reserva iniciada"));
    }

    @PutMapping("/{id}/completar")
    @Operation(summary = "Completar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> completar(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.completar(id), "Reserva completada"));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> cancelar(@PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.cancelar(id, motivo), "Reserva cancelada"));
    }
}
