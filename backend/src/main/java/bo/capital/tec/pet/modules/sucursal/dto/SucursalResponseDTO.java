package bo.capital.tec.pet.modules.sucursal.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SucursalResponseDTO {
    private Long id;
    private String nombre;
    private Long direccionId;
    private String direccionCompleta;
    private String telefono;
    private String email;
    private LocalTime horarioApertura;
    private LocalTime horarioCierre;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private Boolean activa;
    private LocalDateTime createdAt;
}
