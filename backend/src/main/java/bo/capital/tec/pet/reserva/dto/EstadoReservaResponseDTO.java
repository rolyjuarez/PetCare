package bo.capital.tec.pet.reserva.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EstadoReservaResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String color;
    private String icono;
    private Integer orden;
}
