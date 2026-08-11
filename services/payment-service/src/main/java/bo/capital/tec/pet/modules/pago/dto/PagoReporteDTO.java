package bo.capital.tec.pet.modules.pago.dto;

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
public class PagoReporteDTO {
    private Long id;
    private String codigo;
    private Long reservaId;
    private String reservaCodigo;
    private String clienteNombre;
    private BigDecimal monto;
    private BigDecimal montoDescuento;
    private BigDecimal montoTotal;
    private String metodoPago;
    private String estadoPago;
    private LocalDateTime fechaPago;
    private String referencia;
    private LocalDateTime createdAt;
}
