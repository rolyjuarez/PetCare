package bo.capital.tec.pet.modules.usuario.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UsuarioSummaryDTO {
    private Long id;
    private String username;
    private String personaNombre;
    private Boolean activo;
}
