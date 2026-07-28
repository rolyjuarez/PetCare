package bo.capital.tec.pet.modules.mascota.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EspecieSummaryDTO {
    private Long id;
    private String nombre;
}
