package bo.capital.tec.pet.modules.mascota.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MascotaRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @NotNull
    private LocalDate fechaNacimiento;
    @NotBlank @Pattern(regexp = "^[MFO]$")
    private String genero;
    private BigDecimal peso;
    @Size(max = 50)
    private String color;
    private String imagenUrl;
    @NotNull
    private Long especieId;
    @NotNull
    private Long razaId;
    @NotNull
    private Long clienteId;
}
