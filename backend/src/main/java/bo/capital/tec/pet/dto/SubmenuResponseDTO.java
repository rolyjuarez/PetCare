package bo.capital.tec.pet.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmenuResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private String url;
    private Integer orden;
    private Boolean activo;
}
