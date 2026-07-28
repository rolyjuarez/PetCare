package bo.capital.tec.pet.modules.mascota.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MascotaResponseDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String genero;
    private BigDecimal peso;
    private String color;
    private String imagenUrl;
    private Long especieId;
    private String especieNombre;
    private Long razaId;
    private String razaNombre;
    private Long clienteId;
    private String clienteNombre;
    private LocalDateTime createdAt;
}
