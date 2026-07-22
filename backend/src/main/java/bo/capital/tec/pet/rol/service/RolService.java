package bo.capital.tec.pet.rol.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.rol.dto.RolRequestDTO;
import bo.capital.tec.pet.rol.dto.RolResponseDTO;
import bo.capital.tec.pet.rol.dto.RolSummaryDTO;

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
