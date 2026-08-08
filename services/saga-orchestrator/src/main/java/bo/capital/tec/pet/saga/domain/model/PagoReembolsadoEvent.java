package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagoReembolsadoEvent extends DomainEvent {

    private Long pagoId;
    private Long reservaId;
    private BigDecimal monto;
    private String referenciaTransaccion;

    public PagoReembolsadoEvent(Long pagoId, Long reservaId, BigDecimal monto,
                                String referenciaTransaccion) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.monto = monto;
        this.referenciaTransaccion = referenciaTransaccion;
    }
}
