package bo.capital.tec.pet.sucursal.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.sucursal.dto.SucursalRequestDTO;
import bo.capital.tec.pet.sucursal.dto.SucursalResponseDTO;
import bo.capital.tec.pet.sucursal.dto.SucursalSummaryDTO;
import bo.capital.tec.pet.sucursal.entity.Sucursal;
import bo.capital.tec.pet.sucursal.mapper.SucursalMapper;
import bo.capital.tec.pet.sucursal.service.SucursalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalMapper sucursalMapper;

    @Override
    @Transactional
    public SucursalResponseDTO create(SucursalRequestDTO dto) {
        Sucursal sucursal = Sucursal.builder()
                .nombre(dto.getNombre())
                .direccionId(dto.getDireccionId())
                .telefono(dto.getTelefono())
                .email(dto.getEmail())
                .horarioApertura(dto.getHorarioApertura())
                .horarioCierre(dto.getHorarioCierre())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();
        sucursalMapper.insert(sucursal);
        return toResponseDTO(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponseDTO getById(Long id) {
        Sucursal sucursal = sucursalMapper.selectById(id);
        if (sucursal == null) {
            throw new EntityNotFoundException("Sucursal", id);
        }
        return toResponseDTO(sucursal);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SucursalSummaryDTO> getAll(String nombre, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Sucursal> sucursales = sucursalMapper.selectAll(nombre, offset, size);
        long total = sucursalMapper.countAll(nombre);
        List<SucursalSummaryDTO> content = sucursales.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<SucursalSummaryDTO>builder()
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
    @Transactional(readOnly = true)
    public List<SucursalSummaryDTO> getActive() {
        List<Sucursal> sucursales = sucursalMapper.selectActive();
        return sucursales.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SucursalResponseDTO update(Long id, SucursalRequestDTO dto) {
        Sucursal sucursal = sucursalMapper.selectById(id);
        if (sucursal == null) {
            throw new EntityNotFoundException("Sucursal", id);
        }
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccionId(dto.getDireccionId());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setEmail(dto.getEmail());
        sucursal.setHorarioApertura(dto.getHorarioApertura());
        sucursal.setHorarioCierre(dto.getHorarioCierre());
        sucursal.setLatitud(dto.getLatitud());
        sucursal.setLongitud(dto.getLongitud());
        if (dto.getActiva() != null) {
            sucursal.setActiva(dto.getActiva());
        }
        sucursalMapper.update(sucursal);
        return toResponseDTO(sucursal);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Sucursal sucursal = sucursalMapper.selectById(id);
        if (sucursal == null) {
            throw new EntityNotFoundException("Sucursal", id);
        }
        sucursalMapper.softDelete(id);
    }

    private SucursalResponseDTO toResponseDTO(Sucursal sucursal) {
        return SucursalResponseDTO.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .direccionId(sucursal.getDireccionId())
                .direccionCompleta("")
                .telefono(sucursal.getTelefono())
                .email(sucursal.getEmail())
                .horarioApertura(sucursal.getHorarioApertura())
                .horarioCierre(sucursal.getHorarioCierre())
                .latitud(sucursal.getLatitud())
                .longitud(sucursal.getLongitud())
                .activa(sucursal.getActiva())
                .createdAt(sucursal.getCreatedAt())
                .build();
    }

    private SucursalSummaryDTO toSummaryDTO(Sucursal sucursal) {
        return SucursalSummaryDTO.builder()
                .id(sucursal.getId())
                .nombre(sucursal.getNombre())
                .telefono(sucursal.getTelefono())
                .activa(sucursal.getActiva())
                .build();
    }
}
