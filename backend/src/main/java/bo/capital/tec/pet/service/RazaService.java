package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.RazaRequestDTO;
import bo.capital.tec.pet.dto.RazaResponseDTO;
import bo.capital.tec.pet.dto.RazaSummaryDTO;

import java.util.List;

public interface RazaService {
    RazaResponseDTO create(RazaRequestDTO dto);
    RazaResponseDTO getById(Long id);
    List<RazaSummaryDTO> getByEspecieId(Long especieId);
    PagedResponse<RazaSummaryDTO> getAll(int page, int size);
    RazaResponseDTO update(Long id, RazaRequestDTO dto);
    void delete(Long id);
}
