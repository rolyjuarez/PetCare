package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RegistroVacunacionRequestDTO {
    @NotNull
    private Long mascotaId;
    @NotNull
    private Long vacunaId;
    @NotNull
    private LocalDate fechaAplicacion;
    @NotNull
    private LocalDate fechaVencimiento;
    @Size(max = 50)
    private String lote;
    @Size(max = 200)
    private String veterinario;
    private String observaciones;
}
