package bo.capital.tec.pet.modules.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromocionInfoDTO {
    private Long id;
    private String codigo;
    private Long proveedorId;
    private Long servicioId;
    private String nombre;
    private String descripcion;
    private String tipo;
    private BigDecimal valor;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
