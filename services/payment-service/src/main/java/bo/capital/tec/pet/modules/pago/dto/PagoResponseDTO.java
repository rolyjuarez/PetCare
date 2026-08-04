package bo.capital.tec.pet.modules.pago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long reservaId;
    private String codigoReserva;
    private BigDecimal monto;
    private BigDecimal montoOriginal;
    private BigDecimal descuentoTotal;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private String modalidadPago;
    private String intencionId;
    private String estadoSync;
    private List<DescuentoAplicadoDTO> descuentosAplicados;
}
