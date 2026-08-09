package bo.capital.tec.pet.modules.pago.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Comando de compensación de la saga: libera el cupo consumido de la promoción
 * asociada al pago cuando este no se concretó o fue reembolsado.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiberarDescuentoCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
    private Long pagoId;
    private Long reservaId;
    private String motivo;

    public LiberarDescuentoCommand(Long pagoId, Long reservaId, String motivo) {
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.motivo = motivo;
    }
}
