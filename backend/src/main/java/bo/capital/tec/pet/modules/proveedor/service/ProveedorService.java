package bo.capital.tec.pet.modules.proveedor.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullCreateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorFullUpdateDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.modules.proveedor.dto.ProveedorSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProveedorService {
    ProveedorResponseDTO create(ProveedorRequestDTO dto);
    ProveedorResponseDTO createFull(ProveedorFullCreateDTO dto);
    ProveedorResponseDTO getById(Long id);
    PagedResponse<ProveedorSummaryDTO> getAll(String nombre, Long especialidadId, boolean activo, int page, int size);
    ProveedorResponseDTO update(Long id, ProveedorRequestDTO dto);
    ProveedorResponseDTO updateFull(Long id, ProveedorFullUpdateDTO dto);
    void delete(Long id);
    boolean hasReservas(Long proveedorId);
    List<ProveedorSummaryDTO> searchNearby(BigDecimal lat, BigDecimal lng, BigDecimal radioKm);
    List<ProveedorSummaryDTO> getByServicioId(Long servicioId);
}
