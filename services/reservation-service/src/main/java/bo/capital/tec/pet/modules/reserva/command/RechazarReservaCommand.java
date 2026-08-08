package bo.capital.tec.pet.modules.reserva.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Comando del orquestador de la saga: registrar el rechazo del proveedor en la
 * reserva.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RechazarReservaCommand {

    private String commandId = UUID.randomUUID().toString();
    private Instant issuedAt = Instant.now();
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
        this.reservaId = reservaId;
        this.codigo = codigo;
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.proveedorEmpresa = proveedorEmpresa;
        this.servicioNombre = servicioNombre;
        this.motivo = motivo;
    }
}
