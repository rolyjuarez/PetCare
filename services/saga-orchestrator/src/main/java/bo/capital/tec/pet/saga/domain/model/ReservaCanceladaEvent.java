package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservaCanceladaEvent extends DomainEvent {

    private Long reservaId;
    private String codigo;
    private Long clienteId;
    private String clienteNombre;
    private String clienteEmail;
    private Long proveedorId;
    private String proveedorEmpresa;
    private Long servicioId;
    private String servicioNombre;
    private String motivo;

    public ReservaCanceladaEvent(Long reservaId, String codigo, Long clienteId, String clienteNombre,
                                 String clienteEmail, Long proveedorId, String proveedorEmpresa,
                                 Long servicioId, String servicioNombre, String motivo) {
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
        this.motivo = motivo;
    }
}
