package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservaAceptadaEvent extends DomainEvent {

    private Long reservaId;
    private String codigo;
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorEmpresa;
    private String servicioNombre;
    private Instant respondidaEn;
    private String comentarioProveedor;

    public ReservaAceptadaEvent(Long reservaId, String codigo, Long proveedorId,
                                String proveedorNombre, String proveedorEmpresa,
                                String servicioNombre, Instant respondidaEn,
                                String comentarioProveedor) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.respondidaEn = respondidaEn;
        this.comentarioProveedor = comentarioProveedor;
    }
}
