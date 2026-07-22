package bo.capital.tec.pet.vacuna.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegistroVacunacionResponseDTO {
    private Long id;
    private Long mascotaId;
    private String mascotaNombre;
    private Long vacunaId;
    private String vacunaNombre;
    private LocalDate fechaAplicacion;
    private LocalDate fechaVencimiento;
    private String lote;
    private String veterinario;
    private String observaciones;
    private LocalDateTime createdAt;
}
