package bo.capital.tec.pet.modules.rol.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.menu.api.MenuApi;
import bo.capital.tec.pet.modules.menu.domain.model.Menu;
import bo.capital.tec.pet.modules.rol.dto.RolRequestDTO;
import bo.capital.tec.pet.modules.rol.dto.RolResponseDTO;
import bo.capital.tec.pet.modules.rol.dto.RolSummaryDTO;
import bo.capital.tec.pet.modules.rol.domain.model.Rol;
import bo.capital.tec.pet.modules.rol.domain.port.RolRepository;
import bo.capital.tec.pet.modules.rol.application.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final MenuApi menuApi;

    @Override
    @Transactional
    public RolResponseDTO create(RolRequestDTO dto) {
        Rol existing = rolRepository.selectByNombre(dto.getNombre());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe un rol con el nombre: " + dto.getNombre());
        }
        Rol rol = Rol.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
        rolRepository.insert(rol);
        return toResponseDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO getById(Long id) {
        Rol rol = rolRepository.selectById(id);
        if (rol == null) {
            throw new EntityNotFoundException("Rol", id);
        }
        return toResponseDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public RolResponseDTO getByName(String nombre) {
        Rol rol = rolRepository.selectByNombre(nombre);
        if (rol == null) {
            throw new EntityNotFoundException("Rol", "nombre", nombre);
        }
        return toResponseDTO(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RolSummaryDTO> getAll(String nombre, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Rol> roles = rolRepository.selectAll(offset, size);
        long total = rolRepository.countAll();
        List<RolSummaryDTO> content = roles.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<RolSummaryDTO>builder()
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
    public RolResponseDTO update(Long id, RolRequestDTO dto) {
        Rol rol = rolRepository.selectById(id);
        if (rol == null) {
            throw new EntityNotFoundException("Rol", id);
        }
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        if (dto.getActivo() != null) {
            rol.setActivo(dto.getActivo());
        }
        rolRepository.update(rol);
        return toResponseDTO(rol);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Rol rol = rolRepository.selectById(id);
        if (rol == null) {
            throw new EntityNotFoundException("Rol", id);
        }
        rolRepository.softDelete(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getMenuIdsByRolId(Long rolId) {
        List<Menu> menus = menuApi.findByRolId(rolId);
        return menus.stream().map(Menu::getId).collect(Collectors.toList());
    }

    private RolResponseDTO toResponseDTO(Rol rol) {
        return RolResponseDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.getActivo())
                .createdAt(rol.getCreatedAt())
                .build();
    }

    private RolSummaryDTO toSummaryDTO(Rol rol) {
        return RolSummaryDTO.builder()
                .id(rol.getId())
                .nombre(rol.getNombre())
                .activo(rol.getActivo())
                .build();
    }
}
