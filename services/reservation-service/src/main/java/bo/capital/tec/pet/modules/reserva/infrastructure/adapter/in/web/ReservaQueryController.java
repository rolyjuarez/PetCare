package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.reserva.application.query.ReservaQueryService;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaQueryController {

    private final ReservaQueryService reservaQueryService;

    @GetMapping("/slots")
    public ResponseEntity<ApiResponse<List<DisponibilidadSlotsDTO>>> getSlots(
            @RequestParam Long proveedorId,
            @RequestParam Long servicioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Long excluirReservaId) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaQueryService.getSlots(proveedorId, servicioId, desde, hasta, excluirReservaId),
                "Disponibilidad obtenida"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(reservaQueryService.getById(id)));
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
                reservaQueryService.getAll(clienteId, mascotaId, servicioId, estado, fechaDesde, fechaHasta, page, size)));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReservaSummaryDTO>>> getByCliente(
            @PathVariable Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaQueryService.getByClienteId(clienteId, page, size)));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReservaSummaryDTO>>> getByProveedor(
            @PathVariable Long proveedorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaQueryService.getByProveedorId(proveedorId, page, size)));
    }
}
