package bo.capital.tec.pet.vacuna.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.vacuna.dto.VacunaRequestDTO;
import bo.capital.tec.pet.vacuna.dto.VacunaResponseDTO;
import bo.capital.tec.pet.vacuna.dto.VacunaSummaryDTO;

public interface VacunaService {
    VacunaResponseDTO create(VacunaRequestDTO dto);
    VacunaResponseDTO getById(Long id);
    PagedResponse<VacunaSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    VacunaResponseDTO update(Long id, VacunaRequestDTO dto);
    void delete(Long id);
}
