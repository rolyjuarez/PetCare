package bo.capital.tec.pet.controller;

import bo.capital.tec.pet.common.response.ApiResponse;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.PagoRequestDTO;
import bo.capital.tec.pet.dto.PagoResponseDTO;
import bo.capital.tec.pet.dto.PagoSummaryDTO;
import bo.capital.tec.pet.service.PagoService;
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
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "API de gestion de pagos")
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear pago")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> create(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(pagoService.create(dto), "Pago creado"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pago por ID")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar pagos")
    public ResponseEntity<ApiResponse<PagedResponse<PagoSummaryDTO>>> getAll(
            @RequestParam(required = false) Long reservaId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String metodoPago,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                pagoService.getAll(reservaId, clienteId, metodoPago, estado, fechaDesde, fechaHasta, page, size)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> update(@PathVariable Long id, @Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.update(id, dto), "Pago actualizado"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pagoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pago eliminado"));
    }

    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar pago")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> confirmarPago(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.confirmarPago(id), "Pago confirmado"));
    }

    @PutMapping("/{id}/reembolsar")
    @Operation(summary = "Reembolsar pago")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> reembolsar(@PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.reembolsar(id, motivo), "Pago reembolsado"));
    }
}
