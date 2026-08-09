package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comando de compensación del orquestador: libera el cupo consumido de una
 * promoción ({@code usos_actuales}) cuando el pago con descuento no se
 * concretó o fue reembolsado.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LiberarDescuentoCommand extends SagaCommand {

    private Long pagoId;
    private Long reservaId;
    private String motivo;

    public LiberarDescuentoCommand(Long pagoId, Long reservaId, String motivo) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.motivo = motivo;
    }

    public static LiberarDescuentoCommand desde(PagoFallidoEvent evento) {
        return new LiberarDescuentoCommand(evento.getPagoId(), evento.getReservaId(),
                evento.getMotivo() != null ? evento.getMotivo() : "El pago no pudo completarse");
    }

    public static LiberarDescuentoCommand desde(PagoReembolsadoEvent evento) {
        return new LiberarDescuentoCommand(evento.getPagoId(), evento.getReservaId(),
                "Pago reembolsado");
    }
}
