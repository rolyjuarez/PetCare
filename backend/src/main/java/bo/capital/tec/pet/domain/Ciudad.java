package bo.capital.tec.pet.domain;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Ciudad {
    private Long id;
    private String nombre;
    private String codigo;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
