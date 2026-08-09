package bo.capital.tec.pet.modules.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoAplicadoDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipo;
    private BigDecimal monto;
    private Long servicioId;
}
