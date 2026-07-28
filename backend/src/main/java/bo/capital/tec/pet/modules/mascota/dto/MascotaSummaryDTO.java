package bo.capital.tec.pet.modules.mascota.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MascotaSummaryDTO {
    private Long id;
    private String nombre;
    private String color;
    private Long especieId;
    private String especieNombre;
    private Long razaId;
    private String razaNombre;
    private BigDecimal peso;
    private String imagenUrl;
    private LocalDate fechaNacimiento;
    private String genero;
    private Long clienteId;
    private String clienteNombre;
}
