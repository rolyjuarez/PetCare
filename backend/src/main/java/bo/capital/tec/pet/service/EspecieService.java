package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.EspecieRequestDTO;
import bo.capital.tec.pet.dto.EspecieResponseDTO;
import bo.capital.tec.pet.dto.EspecieSummaryDTO;

public interface EspecieService {
    EspecieResponseDTO create(EspecieRequestDTO dto);
    EspecieResponseDTO getById(Long id);
    PagedResponse<EspecieSummaryDTO> getAll(int page, int size);
    EspecieResponseDTO update(Long id, EspecieRequestDTO dto);
    void delete(Long id);
}
