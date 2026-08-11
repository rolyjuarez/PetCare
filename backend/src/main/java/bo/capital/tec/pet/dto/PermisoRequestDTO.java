package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermisoRequestDTO {
    @Size(max = 100)
    private String nombre;
    @NotNull
    private Long rolId;
    @NotNull
    private Long menuId;
    private Long submenuId;
    private Boolean crear;
    private Boolean leer;
    private Boolean actualizar;
    private Boolean eliminar;
}
