package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PagoProcesadoEvent extends DomainEvent {

    private Long pagoId;
    private Long reservaId;
    private BigDecimal monto;
    private BigDecimal montoOriginal;
    private BigDecimal descuentoTotal;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;

    public PagoProcesadoEvent(Long pagoId, Long reservaId, BigDecimal monto, BigDecimal montoOriginal,
                              BigDecimal descuentoTotal, String metodoPago, String estadoPago,
                              String referenciaTransaccion) {
        super("PAGO", pagoId);
        this.pagoId = pagoId;
        this.reservaId = reservaId;
        this.monto = monto;
        this.montoOriginal = montoOriginal;
        this.descuentoTotal = descuentoTotal;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.referenciaTransaccion = referenciaTransaccion;
    }
}
