package bo.capital.tec.pet.proveedor.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProveedorRequestDTO {
    @NotNull
    private Long personaId;
    private Long usuarioId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private BigDecimal radioCoberturaKm;
    private String descripcion;
    private List<Long> servicioIds;
}
