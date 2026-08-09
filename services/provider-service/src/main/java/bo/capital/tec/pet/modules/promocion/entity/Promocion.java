package bo.capital.tec.pet.modules.promocion.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Promocion {

    public static final String TIPO_PERCENTAGE = "PERCENTAGE";
    public static final String TIPO_FIXED = "FIXED";

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
    private Integer limiteUsos;
    private Integer usosActuales;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
