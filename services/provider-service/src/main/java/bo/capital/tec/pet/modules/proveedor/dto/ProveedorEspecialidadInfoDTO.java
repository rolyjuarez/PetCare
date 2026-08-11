package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorEspecialidadInfoDTO {
    private Long servicioId;
    private String servicioNombre;
    private Boolean requiereCertificado;
}
