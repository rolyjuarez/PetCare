package bo.capital.tec.pet.modules.pago.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagoReembolsadoEvent extends DomainEvent {

    private Long pagoId;
    private Long reservaId;
    private String reservaCodigo;
    private BigDecimal monto;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private String motivo;

    public PagoReembolsadoEvent(Long pagoId, Long reservaId, String reservaCodigo,
                                BigDecimal monto, Long clienteId, String clienteNombre,
                                String clienteEmail, String motivo) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.reservaCodigo = reservaCodigo;
        this.monto = monto;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
        this.motivo = motivo;
    }
}
