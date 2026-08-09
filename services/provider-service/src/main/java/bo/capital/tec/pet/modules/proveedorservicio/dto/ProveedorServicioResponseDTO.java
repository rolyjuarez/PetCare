package bo.capital.tec.pet.modules.proveedorservicio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorServicioResponseDTO {
    private Long id;
    private Long proveedorId;
    private String proveedorNombre;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer duracionMinutos;
    private BigDecimal precioBase;
    private Boolean requiereCertificado;
    private Boolean activo;
    private List<ModalidadDTO> modalidades;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalidadDTO {
        private Long id;
        private String modalidad;
        private BigDecimal costoAdicional;
        private Boolean activo;
    }
}
