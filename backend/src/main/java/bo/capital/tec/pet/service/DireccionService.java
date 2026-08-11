package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.DireccionRequestDTO;
import bo.capital.tec.pet.dto.DireccionResponseDTO;
import bo.capital.tec.pet.dto.DireccionSummaryDTO;

public interface DireccionService {
    DireccionResponseDTO create(DireccionRequestDTO dto);
    DireccionResponseDTO getById(Long id);
    PagedResponse<DireccionSummaryDTO> getAll(String calle, Long ciudadId, int page, int size);
    DireccionResponseDTO update(Long id, DireccionRequestDTO dto);
    void delete(Long id);
}
