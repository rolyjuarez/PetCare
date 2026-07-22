package bo.capital.tec.pet.persona.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PersonaSummaryDTO {
    private Long id;
    private String nombreCompleto;
    private String ci;
    private String telefono;
    private String email;
}
