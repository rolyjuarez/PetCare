package bo.capital.tec.pet.modules.proveedor.service;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.DisponibilidadResponseDTO;

import java.util.List;

public interface DisponibilidadService {
    DisponibilidadResponseDTO create(DisponibilidadRequestDTO dto);
    DisponibilidadResponseDTO getById(Long id);
    PagedResponse<DisponibilidadResponseDTO> getAll(Long proveedorId, Long servicioId, int page, int size);
    List<DisponibilidadResponseDTO> getByProveedorAndServicio(Long proveedorId, Long servicioId);
    List<DisponibilidadResponseDTO> getByServicio(Long servicioId);
    DisponibilidadResponseDTO update(Long id, DisponibilidadRequestDTO dto);
    void delete(Long id);
}
