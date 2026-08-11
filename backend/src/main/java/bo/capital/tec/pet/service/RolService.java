package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.RolRequestDTO;
import bo.capital.tec.pet.dto.RolResponseDTO;
import bo.capital.tec.pet.dto.RolSummaryDTO;

import java.util.List;

public interface RolService {
    RolResponseDTO create(RolRequestDTO dto);
    RolResponseDTO getById(Long id);
    RolResponseDTO getByName(String nombre);
    PagedResponse<RolSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    RolResponseDTO update(Long id, RolRequestDTO dto);
    void delete(Long id);
    List<Long> getMenuIdsByRolId(Long rolId);
}
