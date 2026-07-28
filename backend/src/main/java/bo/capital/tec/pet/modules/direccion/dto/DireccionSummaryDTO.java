package bo.capital.tec.pet.modules.direccion.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DireccionSummaryDTO {
    private Long id;
    private String calle;
    private String numero;
    private Long ciudadId;
    private String ciudadNombre;
}
