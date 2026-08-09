package bo.capital.tec.pet.modules.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicioInfoDTO {
    private Long id;
    private String nombre;
    private Integer duracionMinutos;
    private BigDecimal precioBase;
    private String categoria;
    private Boolean requiereCertificado;
}
