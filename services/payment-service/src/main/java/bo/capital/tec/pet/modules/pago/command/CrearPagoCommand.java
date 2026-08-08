package bo.capital.tec.pet.modules.pago.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Comando del orquestador de la saga: crear el pago de una reserva confirmada.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrearPagoCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;
    private Long mascotaId;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private BigDecimal precioTotal;
    private String modalidadEntrega;
    private Long registroVacunacionId;

    public CrearPagoCommand(Long reservaId, String codigo, Long clienteId, Long proveedorId,
                            Long servicioId, Long mascotaId, LocalDate fechaInicio,
                            LocalTime horaInicio, BigDecimal precioTotal,
                            String modalidadEntrega, Long registroVacunacionId) {
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.clienteId = clienteId;
        this.proveedorId = proveedorId;
        this.servicioId = servicioId;
        this.mascotaId = mascotaId;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.precioTotal = precioTotal;
        this.modalidadEntrega = modalidadEntrega;
        this.registroVacunacionId = registroVacunacionId;
    }
}
