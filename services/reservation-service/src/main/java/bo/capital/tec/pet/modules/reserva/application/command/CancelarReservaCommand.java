package bo.capital.tec.pet.modules.reserva.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Comando de compensación del orquestador de la saga: cancelar la reserva
 * cuando el flujo falla (pago rechazado, proveedor rechaza, etc.).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelarReservaCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
    private Long reservaId;
    private String codigo;
    private String motivo;

    public CancelarReservaCommand(Long reservaId, String codigo, String motivo) {
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.motivo = motivo;
    }
}
