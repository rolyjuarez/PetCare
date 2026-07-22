package bo.capital.tec.pet.menu.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 200)
    private String descripcion;
    private String icono;
    private String url;
    private Integer orden;
    private Boolean activo;
}
