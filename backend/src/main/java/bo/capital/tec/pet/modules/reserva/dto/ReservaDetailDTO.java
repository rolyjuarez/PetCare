package bo.capital.tec.pet.modules.reserva.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservaDetailDTO {
    private Long id;
    private String codigo;
    private String clienteNombre;
    private String clienteTelefono;
    private String proveedorNombre;
    private String proveedorTelefono;
    private String servicioNombre;
    private Integer servicioDuracion;
    private String mascotaNombre;
    private String mascotaEspecie;
    private String estadoNombre;
    private String estadoColor;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
    private String notas;
    private BigDecimal precioTotal;
    private LocalDateTime createdAt;
    private PagoResumenDTO pago;
}
