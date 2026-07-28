package bo.capital.tec.pet.modules.pago.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pago {
    private Long id;
    private Long reservaId;
    private BigDecimal monto;
    private String metodoPago;
    private String estadoPago;
    private String referenciaTransaccion;
    private LocalDateTime fechaPago;
    private String comprobanteUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
