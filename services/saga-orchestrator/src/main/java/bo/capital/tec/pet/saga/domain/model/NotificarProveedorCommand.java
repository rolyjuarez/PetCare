package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Comando del orquestador: notificar al proveedor que existe una nueva reserva.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificarProveedorCommand extends SagaCommand {

    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long proveedorId;
    private String proveedorEmpresa;
    private Long servicioId;
    private String servicioNombre;
    private Long mascotaId;
    private String mascotaNombre;
    private LocalDate fechaInicio;
    private LocalTime horaInicio;
    private BigDecimal precioTotal;
    private Long registroVacunacionId;
    private String modalidadEntrega;

    public NotificarProveedorCommand(Long reservaId, String codigo, Long clienteId, String clienteNombre,
                                     String clienteEmail, Long proveedorId, String proveedorEmpresa,
                                     Long servicioId, String servicioNombre, Long mascotaId,
                                     String mascotaNombre, LocalDate fechaInicio, LocalTime horaInicio,
                                     BigDecimal precioTotal, Long registroVacunacionId,
                                     String modalidadEntrega) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.clienteEmail = clienteEmail;
        this.proveedorId = proveedorId;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.mascotaId = mascotaId;
        this.mascotaNombre = mascotaNombre;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.precioTotal = precioTotal;
        this.registroVacunacionId = registroVacunacionId;
        this.modalidadEntrega = modalidadEntrega;
    }

    public static NotificarProveedorCommand desde(ReservaCreadaEvent evento) {
        return new NotificarProveedorCommand(
                evento.getReservaId(), evento.getCodigo(),
                evento.getClienteId(), evento.getClienteNombre(), evento.getClienteEmail(),
                evento.getProveedorId(), evento.getProveedorEmpresa(),
                evento.getServicioId(), evento.getServicioNombre(),
                evento.getMascotaId(), evento.getMascotaNombre(),
                evento.getFechaInicio(), evento.getHoraInicio(),
                evento.getPrecioTotal(), evento.getRegistroVacunacionId(),
                evento.getModalidadEntrega());
    }
}
