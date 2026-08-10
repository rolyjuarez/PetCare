package bo.capital.tec.pet.modules.reserva.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reserva {
    private Long id;
    private String codigo;
    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;
    private Long mascotaId;
    private Long estadoReservaId;
    private Long registroVacunacionId;
    private String modalidadEntrega;
    private LocalDate fechaReserva;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String direccionReferencia;
    private String notas;
    private BigDecimal precioTotal;
    private String motivoRechazo;
    private LocalDateTime respuestaEn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted;
    private Integer version;
}
