package bo.capital.tec.pet.modules.proveedor.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Comando del orquestador de la saga: notificar al proveedor que existe una
 * nueva reserva y crear las solicitudes correspondientes.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificarProveedorCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
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
}
