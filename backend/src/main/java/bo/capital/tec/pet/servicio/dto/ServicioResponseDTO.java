package bo.capital.tec.pet.servicio.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ServicioResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer duracionMinutos;
    private BigDecimal precioBase;
    private String imagenUrl;
    private Boolean activo;
    private String categoria;
    private LocalDateTime createdAt;
}
