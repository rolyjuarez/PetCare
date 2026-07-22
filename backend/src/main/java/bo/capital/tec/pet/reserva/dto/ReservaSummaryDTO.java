package bo.capital.tec.pet.reserva.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservaSummaryDTO {
    private Long id;
    private String codigo;
    private String clienteNombre;
    private String proveedorNombre;
    private String servicioNombre;
    private String estadoNombre;
    private String estadoColor;
    private LocalDate fechaReserva;
    private BigDecimal precioTotal;
}
