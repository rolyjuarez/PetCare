package bo.capital.tec.pet.modules.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaInfoDTO {
    private Long id;
    private String nombre;
    private String especie;
    private Long clienteId;
}
