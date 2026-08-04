package bo.capital.tec.pet.modules.promocion.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PromocionResponseDTO {
    private Long id;
    private Long proveedorId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activa;
    private Integer limiteUsos;
    private Integer usosActuales;
    private LocalDateTime createdAt;
}
