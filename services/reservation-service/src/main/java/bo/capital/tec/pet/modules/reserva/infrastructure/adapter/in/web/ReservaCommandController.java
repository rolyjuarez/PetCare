package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.in.web;

import bo.capital.tec.pet.common.api.ApiResponse;
import bo.capital.tec.pet.modules.reserva.application.command.ActualizarReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.CrearReservaCommand;
import bo.capital.tec.pet.modules.reserva.application.command.ReservaCommandUseCase;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaCommandController {

    private final ReservaCommandUseCase reservaCommandUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> crear(
            @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        reservaCommandUseCase.crear(CrearReservaCommand.desde(dto)), "Reserva creada"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> actualizar(
            @PathVariable Long id, @Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaCommandUseCase.actualizar(id, ActualizarReservaCommand.desde(dto)),
                "Reserva actualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        reservaCommandUseCase.eliminar(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reserva eliminada"));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> cancelar(@PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        return ResponseEntity.ok(ApiResponse.success(
                reservaCommandUseCase.cancelar(id, motivo), "Reserva cancelada"));
    }
}
