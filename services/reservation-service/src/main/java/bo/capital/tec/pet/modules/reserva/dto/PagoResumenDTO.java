package bo.capital.tec.pet.modules.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResumenDTO {
    private Long id;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private LocalDateTime fechaPago;
}
