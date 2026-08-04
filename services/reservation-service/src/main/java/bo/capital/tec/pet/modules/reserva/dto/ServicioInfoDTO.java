package bo.capital.tec.pet.modules.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServicioInfoDTO {
    private Long id;
    private String nombre;
    private Integer duracionMinutos;
    private java.math.BigDecimal precioBase;
    private String categoria;
    private Boolean requiereCertificado;
}
