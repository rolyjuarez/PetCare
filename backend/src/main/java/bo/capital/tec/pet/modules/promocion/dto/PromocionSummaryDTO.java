package bo.capital.tec.pet.modules.promocion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PromocionSummaryDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaFin;
    private Boolean activa;
}
