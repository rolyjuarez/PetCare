package bo.capital.tec.pet.modules.pago.controller;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.ProcesarPagoRequestDTO;
import bo.capital.tec.pet.modules.pago.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.getById(id), "Pago obtenido"));
    }

    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> getByReservaId(@PathVariable Long reservaId) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.getByReservaId(reservaId), "Pago obtenido"));
    }

    @GetMapping("/reserva/{reservaId}/historial")
    public ResponseEntity<ApiResponse<PagedResponse<PagoResponseDTO>>> historial(
            @PathVariable Long reservaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listByReservaId(reservaId, page, size),
                "Historial de pagos"));
    }

    @PostMapping("/{id}/procesar")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> procesar(@PathVariable Long id,
                                                                 @Valid @RequestBody ProcesarPagoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(pagoService.procesar(id, request), "Pago procesado"));
    }

    @PostMapping("/{id}/reembolsar")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> reembolsar(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(pagoService.reembolsar(id), "Pago reembolsado"));
    }
}
