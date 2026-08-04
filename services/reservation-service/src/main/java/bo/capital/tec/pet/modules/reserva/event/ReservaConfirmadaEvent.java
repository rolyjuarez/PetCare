package bo.capital.tec.pet.modules.reserva.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservaConfirmadaEvent extends DomainEvent {

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

    public ReservaConfirmadaEvent(Long reservaId, String codigo, Long clienteId, Long proveedorId,
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
}
