package bo.capital.tec.pet.modules.promocion.dto;

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
public class PromocionSummaryDTO {

    private Long id;
    private Long proveedorId;
    private Long servicioId;
    private String servicioNombre;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activa;
}
