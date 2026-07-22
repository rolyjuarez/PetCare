package bo.capital.tec.pet.promocion.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PromocionLocal {
    private Long id;
    private Long promocionId;
    private Long reservaId;
    private BigDecimal montoDescuento;
    private LocalDateTime fechaAplicacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
