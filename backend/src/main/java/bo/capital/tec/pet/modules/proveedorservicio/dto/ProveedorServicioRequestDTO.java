package bo.capital.tec.pet.modules.proveedorservicio.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorServicioRequestDTO {
    @NotBlank @Size(max = 100)
    private String nombre;
    @Size(max = 1000)
    private String descripcion;
    @NotBlank
    private String categoria;
    @NotNull @Min(15) @Max(1440)
    private Integer duracionMinutos;
    @NotNull @DecimalMin("0.00")
    private BigDecimal precioBase;
    private Boolean requiereCertificado;
    private Boolean activo;
    @NotEmpty
    private List<ModalidadDTO> modalidades;
}
