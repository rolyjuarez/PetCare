package bo.capital.tec.pet.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.dto.PermisoRequestDTO;
import bo.capital.tec.pet.dto.PermisoResponseDTO;
import java.util.List;

public interface PermisoService {
    PermisoResponseDTO create(PermisoRequestDTO dto);
    PermisoResponseDTO getById(Long id);
    PagedResponse<PermisoResponseDTO> getAll(Long rolId, Long menuId, int page, int size);
    PermisoResponseDTO update(Long id, PermisoRequestDTO dto);
    void delete(Long id);
    List<PermisoResponseDTO> getByRolId(Long rolId);
}
