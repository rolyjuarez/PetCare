package bo.capital.tec.pet.modules.menu.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String url;
    private Integer orden;
    private Boolean activo;
    private List<SubmenuResponseDTO> submenus;
}
