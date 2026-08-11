package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.DisponibilidadRequestDTO;
import bo.capital.tec.pet.dto.DisponibilidadResponseDTO;

import java.util.List;

public interface DisponibilidadService {
    DisponibilidadResponseDTO create(DisponibilidadRequestDTO dto);
    DisponibilidadResponseDTO getById(Long id);
    PagedResponse<DisponibilidadResponseDTO> getAll(Long proveedorId, Long servicioId, int page, int size);
    List<DisponibilidadResponseDTO> getByProveedorAndServicio(Long proveedorId, Long servicioId);
    DisponibilidadResponseDTO update(Long id, DisponibilidadRequestDTO dto);
    void delete(Long id);
}
