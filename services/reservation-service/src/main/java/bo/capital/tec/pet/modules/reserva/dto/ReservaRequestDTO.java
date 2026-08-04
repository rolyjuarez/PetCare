package bo.capital.tec.pet.modules.reserva.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {
    @NotNull
    private Long clienteId;
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
    private Long registroVacunacionId;
    private String modalidadEntrega;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
    private String notas;
    private BigDecimal precioTotal;
}
