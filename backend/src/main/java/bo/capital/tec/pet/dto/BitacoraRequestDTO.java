package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BitacoraRequestDTO {
    private Long usuarioId;
    @NotBlank @Size(max = 50)
    private String accion;
    @NotBlank @Size(max = 100)
    private String entidad;
    private Long entidadId;
    @Size(max = 5000)
    private String datosAnteriores;
    @Size(max = 5000)
    private String datosNuevos;
    @Size(max = 45)
    private String ipAddress;
    @Size(max = 500)
    private String userAgent;
}
