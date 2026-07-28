package bo.capital.tec.pet.modules.reserva.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservaSummaryDTO {
    private Long id;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorEmpresa;
    private Long servicioId;
    private String servicioNombre;
    private Long mascotaId;
    private String mascotaNombre;
    private Long estadoReservaId;
    private String estadoReservaNombre;
    private String estadoReservaColor;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precioTotal;
}
