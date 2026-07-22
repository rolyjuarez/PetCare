package bo.capital.tec.pet.pago.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PagoResponseDTO {
    private Long id;
    private Long reservaId;
    private String reservaCodigo;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private String comprobanteUrl;
    private LocalDateTime createdAt;
}
