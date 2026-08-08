package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comando del orquestador: registrar el rechazo del proveedor en la reserva.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RechazarReservaCommand extends SagaCommand {

    private Long reservaId;
    private String codigo;
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorEmpresa;
    private String servicioNombre;
    private String motivo;

    public RechazarReservaCommand(Long reservaId, String codigo, Long proveedorId,
                                  String proveedorNombre, String proveedorEmpresa,
                                  String servicioNombre, String motivo) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.motivo = motivo;
    }

    public static RechazarReservaCommand desde(ReservaRechazadaEvent evento) {
        return new RechazarReservaCommand(
                evento.getReservaId(), evento.getCodigo(),
                evento.getProveedorId(), evento.getProveedorNombre(),
                evento.getProveedorEmpresa(), evento.getServicioNombre(),
                evento.getMotivoRechazo());
    }
}
