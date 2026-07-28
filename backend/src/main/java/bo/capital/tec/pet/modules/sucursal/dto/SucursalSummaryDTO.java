package bo.capital.tec.pet.modules.sucursal.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SucursalSummaryDTO {
    private Long id;
    private String nombre;
    private String telefono;
    private Boolean activa;
}
