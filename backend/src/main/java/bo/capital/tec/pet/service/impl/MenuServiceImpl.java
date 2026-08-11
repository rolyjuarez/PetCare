package bo.capital.tec.pet.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.dto.MenuRequestDTO;
import bo.capital.tec.pet.dto.MenuResponseDTO;
import bo.capital.tec.pet.dto.MenuSummaryDTO;
import bo.capital.tec.pet.dto.SubmenuResponseDTO;
import bo.capital.tec.pet.domain.Menu;
import bo.capital.tec.pet.domain.Submenu;
import bo.capital.tec.pet.repository.MenuMapper;
import bo.capital.tec.pet.repository.SubmenuMapper;
import bo.capital.tec.pet.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;
    private final SubmenuMapper submenuMapper;

    @Override
    @Transactional
    public MenuResponseDTO create(MenuRequestDTO dto) {
        Menu menu = Menu.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .icono(dto.getIcono())
                .url(dto.getUrl())
                .orden(dto.getOrden())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        menuMapper.insert(menu);
        return toResponseDTO(menu, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponseDTO getById(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new EntityNotFoundException("Menu", id);
        }
        List<Submenu> submenus = submenuMapper.selectByMenuId(id);
        return toResponseDTO(menu, submenus);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MenuSummaryDTO> getAll(String nombre, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Menu> menus = menuMapper.selectAll(offset, size);
        long total = menuMapper.countAll();
        List<MenuSummaryDTO> content = menus.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<MenuSummaryDTO>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(total)
                .totalPages((int) Math.ceil((double) total / size))
                .first(page == 0)
                .last((long) (page + 1) * size >= total)
                .build();
    }

    @Override
    @Transactional
    public MenuResponseDTO update(Long id, MenuRequestDTO dto) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new EntityNotFoundException("Menu", id);
        }
        menu.setNombre(dto.getNombre());
        menu.setDescripcion(dto.getDescripcion());
        menu.setIcono(dto.getIcono());
        menu.setUrl(dto.getUrl());
        menu.setOrden(dto.getOrden());
        if (dto.getActivo() != null) {
            menu.setActivo(dto.getActivo());
        }
        menuMapper.update(menu);
        List<Submenu> submenus = submenuMapper.selectByMenuId(id);
        return toResponseDTO(menu, submenus);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Menu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new EntityNotFoundException("Menu", id);
        }
        menuMapper.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> getMenusByRoleId(Long rolId) {
        List<Menu> menus = menuMapper.findByRolId(rolId);
        return menus.stream()
                .map(m -> {
                    List<Submenu> submenus = submenuMapper.selectByMenuId(m.getId());
                    return toResponseDTO(m, submenus);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> getMenusByUsername(String username) {
        List<Menu> menus = menuMapper.findByUsername(username);
        return menus.stream()
                .map(m -> {
                    List<Submenu> submenus = submenuMapper.selectByMenuIdAndUsername(m.getId(), username);
                    return toResponseDTO(m, submenus);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponseDTO> getMenuTree() {
        List<Menu> menus = menuMapper.selectAll(0, 1000);
        return menus.stream()
                .map(m -> {
                    List<Submenu> submenus = submenuMapper.selectByMenuId(m.getId());
                    return toResponseDTO(m, submenus);
                })
                .collect(Collectors.toList());
    }

    private MenuResponseDTO toResponseDTO(Menu menu, List<Submenu> submenus) {
        List<SubmenuResponseDTO> submenuDTOs = submenus.stream()
                .map(s -> SubmenuResponseDTO.builder()
                        .id(s.getId())
                        .nombre(s.getNombre())
                        .descripcion(s.getDescripcion())
                        .icono(s.getIcono())
                        .url(s.getUrl())
                        .orden(s.getOrden())
                        .activo(s.getActivo())
                        .build())
                .collect(Collectors.toList());
        return MenuResponseDTO.builder()
                .id(menu.getId())
                .nombre(menu.getNombre())
                .descripcion(menu.getDescripcion())
                .icono(menu.getIcono())
                .url(menu.getUrl())
                .orden(menu.getOrden())
                .activo(menu.getActivo())
                .submenus(submenuDTOs)
                .build();
    }

    private MenuSummaryDTO toSummaryDTO(Menu menu) {
        return MenuSummaryDTO.builder()
                .id(menu.getId())
                .nombre(menu.getNombre())
                .icono(menu.getIcono())
                .url(menu.getUrl())
                .orden(menu.getOrden())
                .build();
    }
}
