package bo.capital.tec.pet.modules.proveedorservicio.service;

import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioRequestDTO;
import bo.capital.tec.pet.modules.proveedorservicio.dto.ProveedorServicioResponseDTO;

import java.util.List;

public interface ProveedorServicioService {
    ProveedorServicioResponseDTO create(Long proveedorId, ProveedorServicioRequestDTO dto);
    ProveedorServicioResponseDTO update(Long proveedorId, Long id, ProveedorServicioRequestDTO dto);
    ProveedorServicioResponseDTO setActivo(Long proveedorId, Long id, Boolean activo);
    void delete(Long proveedorId, Long id);
    ProveedorServicioResponseDTO getById(Long proveedorId, Long id);
    List<ProveedorServicioResponseDTO> listByProveedor(Long proveedorId);
    List<ProveedorServicioResponseDTO> listActivosByProveedor(Long proveedorId);
    List<ProveedorServicioResponseDTO> listActivosByCategoria(String categoria);
}
