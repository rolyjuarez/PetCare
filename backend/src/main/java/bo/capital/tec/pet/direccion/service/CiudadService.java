package bo.capital.tec.pet.direccion.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.direccion.dto.CiudadRequestDTO;
import bo.capital.tec.pet.direccion.dto.CiudadResponseDTO;
import bo.capital.tec.pet.direccion.dto.CiudadSummaryDTO;

public interface CiudadService {
    CiudadResponseDTO create(CiudadRequestDTO dto);
    CiudadResponseDTO getById(Long id);
    PagedResponse<CiudadSummaryDTO> getAll(String nombre, Long departamentoId, int page, int size);
    CiudadResponseDTO update(Long id, CiudadRequestDTO dto);
    void delete(Long id);
}
