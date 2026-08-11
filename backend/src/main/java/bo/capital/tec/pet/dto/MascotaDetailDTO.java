package bo.capital.tec.pet.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MascotaDetailDTO {
    private Long id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String genero;
    private BigDecimal peso;
    private String color;
    private String imagenUrl;
    private String especieNombre;
    private String razaNombre;
    private String clienteNombre;
    private List<RegistroVacunacionDTO> vacunas;
    private LocalDateTime createdAt;
}
