package bo.capital.tec.pet.mascota.dto;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegistroVacunacionDTO {
    private Long id;
    private String vacunaNombre;
    private LocalDate fechaAplicacion;
    private LocalDate fechaVencimiento;
    private String veterinario;
}
