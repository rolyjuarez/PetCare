package bo.capital.tec.pet.modules.proveedor.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullCreateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullUpdateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorSummaryDTO;

public interface ProveedorCrudService {
    ProveedorResponseDTO create(ProveedorRequestDTO dto);
    ProveedorResponseDTO createFull(ProveedorFullCreateDTO dto);
    ProveedorResponseDTO getById(Long id);
    ProveedorResponseDTO getByUsuarioId(Long usuarioId);
    ProveedorResponseDTO setRequiereCertificado(Long proveedorId, Long servicioId, Boolean requiere);
    PagedResponse<ProveedorSummaryDTO> getAll(String nombre, int page, int size);
    ProveedorResponseDTO update(Long id, ProveedorRequestDTO dto);
    ProveedorResponseDTO updateFull(Long id, ProveedorFullUpdateDTO dto);
    void delete(Long id);
    boolean hasReservas(Long proveedorId);
}
