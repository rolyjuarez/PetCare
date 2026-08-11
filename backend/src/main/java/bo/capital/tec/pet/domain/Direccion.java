package bo.capital.tec.pet.domain;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Direccion {
    private Long id;
    private String calle;
    private String numero;
    private String piso;
    private String apartamento;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String referencia;
    private Long ciudadId;
    private Long estadoId;
    private Ciudad ciudad;
    private Estado estado;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
