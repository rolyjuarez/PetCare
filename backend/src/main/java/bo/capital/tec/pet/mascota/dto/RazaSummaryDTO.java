package bo.capital.tec.pet.mascota.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RazaSummaryDTO {
    private Long id;
    private String nombre;
    private String especieNombre;
}
