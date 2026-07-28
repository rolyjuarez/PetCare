package bo.capital.tec.pet.modules.rol.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RolSummaryDTO {
    private Long id;
    private String nombre;
    private Boolean activo;
}
