package bo.capital.tec.pet.modules.vacuna.entity;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RegistroVacunacion {
    private Long id;
    private Long mascotaId;
    private Long vacunaId;
    private LocalDate fechaAplicacion;
    private LocalDate fechaVencimiento;
    private String lote;
    private String veterinario;
    private String observaciones;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
