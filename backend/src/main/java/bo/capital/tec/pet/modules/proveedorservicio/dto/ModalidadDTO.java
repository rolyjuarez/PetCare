package bo.capital.tec.pet.modules.proveedorservicio.dto;

import lombok.*;

import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ModalidadDTO {
    private String modalidad;
    private BigDecimal costoAdicional;
}
