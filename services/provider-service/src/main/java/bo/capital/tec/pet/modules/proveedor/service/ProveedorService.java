package bo.capital.tec.pet.modules.proveedor.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.command.NotificarProveedorCommand;
import bo.capital.tec.pet.modules.proveedor.dto.ResponderSolicitudRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.SolicitudReservaResponseDTO;

public interface ProveedorService {

    PagedResponse<SolicitudReservaResponseDTO> listarSolicitudes(Long proveedorId, String estado, int page, int size);

    SolicitudReservaResponseDTO aceptar(Long proveedorId, Long solicitudId, ResponderSolicitudRequestDTO request);

    SolicitudReservaResponseDTO rechazar(Long proveedorId, Long solicitudId, ResponderSolicitudRequestDTO request);

    void procesarNotificarProveedor(NotificarProveedorCommand comando);
}
