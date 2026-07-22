package bo.capital.tec.pet.cliente.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClienteSummaryDTO {
    private Long id;
    private String nombreCompleto;
    private String ci;
    private String telefono;
}
