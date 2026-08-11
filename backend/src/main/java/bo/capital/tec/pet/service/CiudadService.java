package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.CiudadRequestDTO;
import bo.capital.tec.pet.dto.CiudadResponseDTO;
import bo.capital.tec.pet.dto.CiudadSummaryDTO;

public interface CiudadService {
    CiudadResponseDTO create(CiudadRequestDTO dto);
    CiudadResponseDTO getById(Long id);
    PagedResponse<CiudadSummaryDTO> getAll(String nombre, Long departamentoId, int page, int size);
    CiudadResponseDTO update(Long id, CiudadRequestDTO dto);
    void delete(Long id);
}
