package bo.capital.tec.pet.modules.vacuna.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class VacunaSummaryDTO {
    private Long id;
    private String nombre;
    private Integer periodicidadMeses;
}
