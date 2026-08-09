package bo.capital.tec.pet.common.client;

import bo.capital.tec.pet.modules.proveedor.dto.ReservaUbicacionDTO;
import bo.capital.tec.pet.modules.proveedor.dto.VacunaInfoDTO;

public interface ReservationInternalClient {

    VacunaInfoDTO getRegistroVacunacion(Long id);

    ReservaUbicacionDTO getUbicacionReserva(Long reservaId);

    long countReservasByProveedor(Long proveedorId);
}
