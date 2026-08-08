package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagoFallidoEvent extends DomainEvent {

    private Long pagoId;
    private Long reservaId;
    private BigDecimal monto;
    private String intencionId;
    private String motivo;

    public PagoFallidoEvent(Long pagoId, Long reservaId, BigDecimal monto,
                            String intencionId, String motivo) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.monto = monto;
        this.intencionId = intencionId;
        this.motivo = motivo;
    }
}
