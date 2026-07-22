package bo.capital.tec.pet.menu.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PermisoResponseDTO {
    private Long id;
    private String nombre;
    private Long rolId;
    private String rolNombre;
    private Long menuId;
    private String menuNombre;
    private Long submenuId;
    private String submenuNombre;
    private Boolean crear;
    private Boolean leer;
    private Boolean actualizar;
    private Boolean eliminar;
}
