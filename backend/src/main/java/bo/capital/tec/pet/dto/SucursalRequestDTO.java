package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SucursalRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @NotNull
    private Long direccionId;
    @NotBlank @Size(max = 20)
    private String telefono;
    @Email
    private String email;
    @NotNull
    private LocalTime horarioApertura;
    @NotNull
    private LocalTime horarioCierre;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Boolean activa;
}
