package bo.capital.tec.pet.modules.reserva.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservaCompletadaEvent extends DomainEvent {

    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long proveedorId;
    private String proveedorEmpresa;
    private Long servicioId;
    private String servicioNombre;
    private BigDecimal precioTotal;

    public ReservaCompletadaEvent(Long reservaId, String codigo, Long clienteId, String clienteNombre,
                                  String clienteEmail, Long proveedorId, String proveedorEmpresa,
                                  Long servicioId, String servicioNombre, BigDecimal precioTotal) {
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
        this.precioTotal = precioTotal;
    }
}
