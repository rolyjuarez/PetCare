package bo.capital.tec.pet.modules.reserva.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> create(@Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(reservaService.create(dto), "Reserva creada"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.getById(id)));
    }

    @GetMapping
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

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReservaSummaryDTO>>> getByCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.getByClienteId(clienteId, page, size)));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReservaSummaryDTO>>> getByProveedor(
            @PathVariable Long proveedorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.getByProveedorId(proveedorId, page, size)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> update(@PathVariable Long id,
                                                                  @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.update(id, dto), "Reserva actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        reservaService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reserva eliminada"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> cancelar(@PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.success(reservaService.cancelar(id, motivo), "Reserva cancelada"));
    }
}
