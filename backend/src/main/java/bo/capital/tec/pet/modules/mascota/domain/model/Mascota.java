package bo.capital.tec.pet.modules.mascota.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Mascota {
    private Long id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String genero;
    private BigDecimal peso;
    private String color;
    private String imagenUrl;
    private Long especieId;
    private Long razaId;
    private Long clienteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
