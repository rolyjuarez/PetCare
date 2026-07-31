package bo.capital.tec.pet.modules.reserva.event;

import bo.capital.tec.pet.common.event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservaRechazadaEvent extends DomainEvent {

    private Long reservaId;
    private String codigo;
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorEmpresa;
    private String servicioNombre;
    private String motivoRechazo;
    private Instant respondidaEn;

    public ReservaRechazadaEvent(Long reservaId, String codigo, Long proveedorId,
                                 String proveedorNombre, String proveedorEmpresa,
                                 String servicioNombre, String motivoRechazo,
                                 Instant respondidaEn) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.motivoRechazo = motivoRechazo;
        this.respondidaEn = respondidaEn;
    }
}
