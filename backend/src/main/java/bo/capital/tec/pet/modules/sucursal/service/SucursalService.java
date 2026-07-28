package bo.capital.tec.pet.modules.sucursal.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.sucursal.dto.SucursalRequestDTO;
import bo.capital.tec.pet.modules.sucursal.dto.SucursalResponseDTO;
import bo.capital.tec.pet.modules.sucursal.dto.SucursalSummaryDTO;

import java.util.List;

public interface SucursalService {
    SucursalResponseDTO create(SucursalRequestDTO dto);
    SucursalResponseDTO getById(Long id);
    PagedResponse<SucursalSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    List<SucursalSummaryDTO> getActive();
    SucursalResponseDTO update(Long id, SucursalRequestDTO dto);
    void delete(Long id);
}
