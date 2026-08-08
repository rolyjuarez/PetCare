package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando del orquestador: crear el pago de una reserva confirmada.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrearPagoCommand extends SagaCommand {

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
        super("RESERVA", reservaId);
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

    public static CrearPagoCommand desde(ReservaConfirmadaEvent evento) {
        return new CrearPagoCommand(
                evento.getReservaId(), evento.getCodigo(),
                evento.getClienteId(), evento.getProveedorId(),
                evento.getServicioId(), evento.getMascotaId(),
                evento.getFechaInicio(), evento.getHoraInicio(),
                evento.getPrecioTotal(), evento.getModalidadEntrega(),
                evento.getRegistroVacunacionId());
    }
}
