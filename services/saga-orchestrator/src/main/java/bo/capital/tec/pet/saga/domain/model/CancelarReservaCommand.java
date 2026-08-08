package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comando de compensación del orquestador: cancelar la reserva cuando el flujo
 * falla (pago rechazado, proveedor rechaza, etc.).
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelarReservaCommand extends SagaCommand {

    private Long reservaId;
    private String codigo;
    private String motivo;

    public CancelarReservaCommand(Long reservaId, String codigo, String motivo) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.motivo = motivo;
    }

    public static CancelarReservaCommand desde(PagoFallidoEvent evento) {
        return new CancelarReservaCommand(evento.getReservaId(), null,
                evento.getMotivo() != null ? evento.getMotivo() : "El pago no pudo completarse");
    }
}
