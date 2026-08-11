package bo.capital.tec.pet.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MenuSummaryDTO {
    private Long id;
    private String nombre;
    private String icono;
    private String url;
    private Integer orden;
}
