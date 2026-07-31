package bo.capital.tec.pet.modules.proveedor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorInfoDTO {
    private Long id;
    private Long personaId;
    private String nombre;
    private String telefono;
    private String empresa;
}
