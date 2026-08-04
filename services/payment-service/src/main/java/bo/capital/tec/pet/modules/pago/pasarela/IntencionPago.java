package bo.capital.tec.pet.modules.pago.pasarela;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntencionPago {
    private Long pagoId;
    private Long reservaId;
    private BigDecimal monto;
    private String metodoPago;
    private String numeroTarjeta;
    private String titularTarjeta;
    private String expiraTarjeta;
    private String cvvTarjeta;
}
