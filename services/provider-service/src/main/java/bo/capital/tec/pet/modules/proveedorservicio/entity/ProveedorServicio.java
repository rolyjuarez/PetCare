package bo.capital.tec.pet.modules.proveedorservicio.entity;

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
public class ProveedorServicio {
    private Long id;
    private Long proveedorId;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer duracionMinutos;
    private BigDecimal precioBase;
    private Boolean requiereCertificado;
    private Boolean activo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
