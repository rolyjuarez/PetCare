package bo.capital.tec.pet.modules.proveedor.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Proveedor {
    private Long id;
    private Long personaId;
    private Long usuarioId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String empresa;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private Boolean verificado;
    private BigDecimal calificacion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
