package bo.capital.tec.pet.modules.menu.service;

import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.modules.menu.dto.MenuRequestDTO;
import bo.capital.tec.pet.modules.menu.dto.MenuResponseDTO;
import bo.capital.tec.pet.modules.menu.dto.MenuSummaryDTO;

import java.util.List;

public interface MenuService {
    MenuResponseDTO create(MenuRequestDTO dto);
    MenuResponseDTO getById(Long id);
    PagedResponse<MenuSummaryDTO> getAll(String nombre, boolean activo, int page, int size);
    MenuResponseDTO update(Long id, MenuRequestDTO dto);
    void delete(Long id);
    List<MenuResponseDTO> getMenusByRoleId(Long rolId);
    List<MenuResponseDTO> getMenusByUsername(String username);
    List<MenuResponseDTO> getMenuTree();
}
