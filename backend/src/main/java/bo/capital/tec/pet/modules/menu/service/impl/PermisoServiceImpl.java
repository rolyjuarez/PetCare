package bo.capital.tec.pet.modules.menu.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.menu.dto.PermisoRequestDTO;
import bo.capital.tec.pet.modules.menu.dto.PermisoResponseDTO;
import bo.capital.tec.pet.modules.menu.entity.Permiso;
import bo.capital.tec.pet.modules.menu.mapper.PermisoMapper;
import bo.capital.tec.pet.modules.menu.service.PermisoService;
import bo.capital.tec.pet.modules.menu.entity.Menu;
import bo.capital.tec.pet.modules.menu.entity.Submenu;
import bo.capital.tec.pet.modules.menu.mapper.MenuMapper;
import bo.capital.tec.pet.modules.menu.mapper.SubmenuMapper;
import bo.capital.tec.pet.modules.rol.entity.Rol;
import bo.capital.tec.pet.modules.rol.mapper.RolMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermisoServiceImpl implements PermisoService {

    private final PermisoMapper permisoMapper;
    private final RolMapper rolMapper;
    private final MenuMapper menuMapper;
    private final SubmenuMapper submenuMapper;

    @Override
    @Transactional
    public PermisoResponseDTO create(PermisoRequestDTO dto) {
        Rol rol = rolMapper.selectById(dto.getRolId());
        if (rol == null) {
            throw new EntityNotFoundException("Rol", dto.getRolId());
        }
        Menu menu = menuMapper.selectById(dto.getMenuId());
        if (menu == null) {
            throw new EntityNotFoundException("Menu", dto.getMenuId());
        }
        if (dto.getSubmenuId() != null && dto.getSubmenuId() > 0) {
            Submenu submenu = submenuMapper.selectById(dto.getSubmenuId());
            if (submenu == null) {
                throw new EntityNotFoundException("Submenu", dto.getSubmenuId());
            }
        }
        Permiso permiso = Permiso.builder()
                .nombre(dto.getNombre())
                .rolId(dto.getRolId())
                .menuId(dto.getMenuId())
                .submenuId(dto.getSubmenuId() != null && dto.getSubmenuId() > 0 ? dto.getSubmenuId() : null)
                .crear(dto.getCrear() != null ? dto.getCrear() : false)
                .leer(dto.getLeer() != null ? dto.getLeer() : false)
                .actualizar(dto.getActualizar() != null ? dto.getActualizar() : false)
                .eliminar(dto.getEliminar() != null ? dto.getEliminar() : false)
                .build();
        permisoMapper.insert(permiso);
        return toResponseDTO(permiso);
    }

    @Override
    @Transactional(readOnly = true)
    public PermisoResponseDTO getById(Long id) {
        Permiso permiso = permisoMapper.selectById(id);
        if (permiso == null) {
            throw new EntityNotFoundException("Permiso", id);
        }
        return toResponseDTO(permiso);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PermisoResponseDTO> getAll(Long rolId, Long menuId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Permiso> permisos = permisoMapper.selectAll(offset, size);
        long total = permisoMapper.countAll();
        List<PermisoResponseDTO> content = permisos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
        return PagedResponse.<PermisoResponseDTO>builder()
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
    public PermisoResponseDTO update(Long id, PermisoRequestDTO dto) {
        Permiso permiso = permisoMapper.selectById(id);
        if (permiso == null) {
            throw new EntityNotFoundException("Permiso", id);
        }
        Rol rol = rolMapper.selectById(dto.getRolId());
        if (rol == null) {
            throw new EntityNotFoundException("Rol", dto.getRolId());
        }
        Menu menu = menuMapper.selectById(dto.getMenuId());
        if (menu == null) {
            throw new EntityNotFoundException("Menu", dto.getMenuId());
        }
        if (dto.getSubmenuId() != null && dto.getSubmenuId() > 0) {
            Submenu submenu = submenuMapper.selectById(dto.getSubmenuId());
            if (submenu == null) {
                throw new EntityNotFoundException("Submenu", dto.getSubmenuId());
            }
        }
        permiso.setNombre(dto.getNombre());
        permiso.setRolId(dto.getRolId());
        permiso.setMenuId(dto.getMenuId());
        permiso.setSubmenuId(dto.getSubmenuId() != null && dto.getSubmenuId() > 0 ? dto.getSubmenuId() : null);
        if (dto.getCrear() != null) permiso.setCrear(dto.getCrear());
        if (dto.getLeer() != null) permiso.setLeer(dto.getLeer());
        if (dto.getActualizar() != null) permiso.setActualizar(dto.getActualizar());
        if (dto.getEliminar() != null) permiso.setEliminar(dto.getEliminar());
        permisoMapper.update(permiso);
        return toResponseDTO(permiso);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permiso permiso = permisoMapper.selectById(id);
        if (permiso == null) {
            throw new EntityNotFoundException("Permiso", id);
        }
        permisoMapper.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermisoResponseDTO> getByRolId(Long rolId) {
        List<Permiso> permisos = permisoMapper.selectByRolId(rolId);
        return permisos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private PermisoResponseDTO toResponseDTO(Permiso permiso) {
        Rol rol = rolMapper.selectById(permiso.getRolId());
        Menu menu = menuMapper.selectById(permiso.getMenuId());
        Submenu submenu = permiso.getSubmenuId() != null
                ? submenuMapper.selectById(permiso.getSubmenuId())
                : null;
        return PermisoResponseDTO.builder()
                .id(permiso.getId())
                .nombre(permiso.getNombre())
                .rolId(permiso.getRolId())
                .rolNombre(rol != null ? rol.getNombre() : null)
                .menuId(permiso.getMenuId())
                .menuNombre(menu != null ? menu.getNombre() : null)
                .submenuId(permiso.getSubmenuId())
                .submenuNombre(submenu != null ? submenu.getNombre() : null)
                .crear(permiso.getCrear())
                .leer(permiso.getLeer())
                .actualizar(permiso.getActualizar())
                .eliminar(permiso.getEliminar())
                .build();
    }
}
