package bo.capital.tec.pet.saga.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Comando del orquestador: confirmar la reserva porque el proveedor la aceptó.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmarReservaCommand extends SagaCommand {

    private Long reservaId;
    private String codigo;
    private Long proveedorId;
    private String proveedorNombre;
    private String proveedorEmpresa;
    private String servicioNombre;
    private String comentario;

    public ConfirmarReservaCommand(Long reservaId, String codigo, Long proveedorId,
                                   String proveedorNombre, String proveedorEmpresa,
                                   String servicioNombre, String comentario) {
        super("RESERVA", reservaId);
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.comentario = comentario;
    }

    public static ConfirmarReservaCommand desde(ReservaAceptadaEvent evento) {
        return new ConfirmarReservaCommand(
                evento.getReservaId(), evento.getCodigo(),
                evento.getProveedorId(), evento.getProveedorNombre(),
                evento.getProveedorEmpresa(), evento.getServicioNombre(),
                evento.getComentarioProveedor());
    }
}
