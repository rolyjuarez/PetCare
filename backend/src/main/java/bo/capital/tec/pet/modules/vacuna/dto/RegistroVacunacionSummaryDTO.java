package bo.capital.tec.pet.modules.vacuna.dto;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegistroVacunacionSummaryDTO {
    private Long id;
    private Long mascotaId;
    private String mascotaNombre;
    private Long vacunaId;
    private String vacunaNombre;
    private LocalDate fechaAplicacion;
    private LocalDate fechaVencimiento;
}
