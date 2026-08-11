package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.VacunaRequestDTO;
import bo.capital.tec.pet.dto.VacunaResponseDTO;
import bo.capital.tec.pet.dto.VacunaSummaryDTO;

public interface VacunaService {
    VacunaResponseDTO create(VacunaRequestDTO dto);
    VacunaResponseDTO getById(Long id);
    PagedResponse<VacunaSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    VacunaResponseDTO update(Long id, VacunaRequestDTO dto);
    void delete(Long id);
}
