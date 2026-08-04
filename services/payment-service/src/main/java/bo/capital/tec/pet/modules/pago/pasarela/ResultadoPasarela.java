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
public class ResultadoPasarela {
    private boolean aprobado;
    private String intencionId;
    private String referenciaTransaccion;
    private String mensaje;
    private BigDecimal montoCobrado;
}
