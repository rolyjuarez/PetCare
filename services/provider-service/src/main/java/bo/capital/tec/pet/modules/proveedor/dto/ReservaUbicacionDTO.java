package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaUbicacionDTO {
    private Long reservaId;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
}
