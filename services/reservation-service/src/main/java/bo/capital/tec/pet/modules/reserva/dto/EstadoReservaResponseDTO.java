package bo.capital.tec.pet.modules.reserva.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoReservaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    private String icono;
    private Integer orden;
}
