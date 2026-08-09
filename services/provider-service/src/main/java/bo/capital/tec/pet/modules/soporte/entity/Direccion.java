package bo.capital.tec.pet.modules.soporte.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
