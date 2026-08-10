package bo.capital.tec.pet.modules.reserva.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Comando del orquestador de la saga: confirmar la reserva porque el proveedor
 * la aceptó.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmarReservaCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
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
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.comentario = comentario;
    }
}
