package bo.capital.tec.pet.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReservaRequestDTO {
    @NotNull
    private Long clienteId;
    @NotNull
    private Long proveedorId;
    @NotNull
    private Long servicioId;
    @NotNull
    private Long mascotaId;
    @NotNull
    private LocalDate fechaReserva;
    @NotNull
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    @NotNull
    private LocalTime horaInicio;
    @NotNull
    private LocalTime horaFin;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
    private String notas;
    private BigDecimal precioTotal;
}
