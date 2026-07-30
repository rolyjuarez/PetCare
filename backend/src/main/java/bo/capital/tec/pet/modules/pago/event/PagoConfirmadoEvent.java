package bo.capital.tec.pet.modules.pago.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagoConfirmadoEvent extends DomainEvent {

    private Long pagoId;
    private Long reservaId;
    private String reservaCodigo;
    private BigDecimal monto;
    private String metodoPago;
    private String referenciaTransaccion;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;

    public PagoConfirmadoEvent(Long pagoId, Long reservaId, String reservaCodigo,
                               BigDecimal monto, String metodoPago, String referenciaTransaccion,
                               Long clienteId, String clienteNombre, String clienteEmail) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.reservaCodigo = reservaCodigo;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referenciaTransaccion = referenciaTransaccion;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
    }
}
