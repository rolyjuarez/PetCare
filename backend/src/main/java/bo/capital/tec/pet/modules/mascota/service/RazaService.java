package bo.capital.tec.pet.modules.mascota.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.mascota.dto.RazaRequestDTO;
import bo.capital.tec.pet.modules.mascota.dto.RazaResponseDTO;
import bo.capital.tec.pet.modules.mascota.dto.RazaSummaryDTO;

import java.util.List;

public interface RazaService {
    RazaResponseDTO create(RazaRequestDTO dto);
    RazaResponseDTO getById(Long id);
    List<RazaSummaryDTO> getByEspecieId(Long especieId);
    PagedResponse<RazaSummaryDTO> getAll(int page, int size);
    RazaResponseDTO update(Long id, RazaRequestDTO dto);
    void delete(Long id);
}
