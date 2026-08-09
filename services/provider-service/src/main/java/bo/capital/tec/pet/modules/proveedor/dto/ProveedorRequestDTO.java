package bo.capital.tec.pet.modules.proveedor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorRequestDTO {
    @NotNull
    private Long personaId;
    private Long usuarioId;
    private String empresa;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private List<Long> servicioIds;
}
