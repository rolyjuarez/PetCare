package bo.capital.tec.pet.proveedor.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.proveedor.dto.ProveedorRequestDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorResponseDTO;
import bo.capital.tec.pet.proveedor.dto.ProveedorSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProveedorService {
    ProveedorResponseDTO create(ProveedorRequestDTO dto);
    ProveedorResponseDTO getById(Long id);
    PagedResponse<ProveedorSummaryDTO> getAll(String nombre, Long especialidadId, boolean activo, int page, int size);
    ProveedorResponseDTO update(Long id, ProveedorRequestDTO dto);
    void delete(Long id);
    List<ProveedorSummaryDTO> searchNearby(BigDecimal lat, BigDecimal lng, BigDecimal radioKm);
    List<ProveedorSummaryDTO> getByServicioId(Long servicioId);
}
