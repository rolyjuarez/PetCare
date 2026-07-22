package bo.capital.tec.pet.cliente.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ClienteResponseDTO {
    private Long id;
    private Long personaId;
    private String personaNombre;
    private String personaApellido;
    private String personaCi;
    private String personaTelefono;
    private String personaEmail;
    private Long usuarioId;
    private String notas;
    private LocalDateTime createdAt;
}
