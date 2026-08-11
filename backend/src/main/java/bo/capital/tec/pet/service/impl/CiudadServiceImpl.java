package bo.capital.tec.pet.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.dto.CiudadRequestDTO;
import bo.capital.tec.pet.dto.CiudadResponseDTO;
import bo.capital.tec.pet.dto.CiudadSummaryDTO;
import bo.capital.tec.pet.domain.Ciudad;
import bo.capital.tec.pet.repository.CiudadMapper;
import bo.capital.tec.pet.service.CiudadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CiudadServiceImpl implements CiudadService {

    private final CiudadMapper ciudadMapper;

    @Override
    @Transactional
    public CiudadResponseDTO create(CiudadRequestDTO dto) {
        Ciudad existing = ciudadMapper.selectByCodigo(dto.getCodigo());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe una ciudad con el codigo: " + dto.getCodigo());
        }
        Ciudad ciudad = Ciudad.builder()
                .nombre(dto.getNombre())
                .codigo(dto.getCodigo())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .build();
        ciudadMapper.insert(ciudad);
        return toResponseDTO(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public CiudadResponseDTO getById(Long id) {
        Ciudad ciudad = ciudadMapper.selectById(id);
        if (ciudad == null) {
            throw new EntityNotFoundException("Ciudad", id);
        }
        return toResponseDTO(ciudad);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<CiudadSummaryDTO> getAll(String nombre, Long departamentoId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Ciudad> ciudades = ciudadMapper.selectAll();
        List<CiudadSummaryDTO> allContent = ciudades.stream()
                .filter(c -> nombre == null || c.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        int total = allContent.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<CiudadSummaryDTO> paged = allContent.subList(fromIndex, toIndex);
        return PagedResponse.<CiudadSummaryDTO>builder()
                .content(paged)
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
    public CiudadResponseDTO update(Long id, CiudadRequestDTO dto) {
        Ciudad ciudad = ciudadMapper.selectById(id);
        if (ciudad == null) {
            throw new EntityNotFoundException("Ciudad", id);
        }
        ciudad.setNombre(dto.getNombre());
        ciudad.setCodigo(dto.getCodigo());
        ciudad.setLatitud(dto.getLatitud());
        ciudad.setLongitud(dto.getLongitud());
        throw new UnsupportedOperationException("CiudadMapper no tiene metodo update. Agregue update a CiudadMapper.");
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Ciudad ciudad = ciudadMapper.selectById(id);
        if (ciudad == null) {
            throw new EntityNotFoundException("Ciudad", id);
        }
        throw new UnsupportedOperationException("CiudadMapper no tiene metodo softDelete. Agregue softDelete a CiudadMapper.");
    }

    private CiudadResponseDTO toResponseDTO(Ciudad ciudad) {
        return CiudadResponseDTO.builder()
                .id(ciudad.getId())
                .nombre(ciudad.getNombre())
                .codigo(ciudad.getCodigo())
                .latitud(ciudad.getLatitud())
                .longitud(ciudad.getLongitud())
                .build();
    }

    private CiudadSummaryDTO toSummaryDTO(Ciudad ciudad) {
        return CiudadSummaryDTO.builder()
                .id(ciudad.getId())
                .nombre(ciudad.getNombre())
                .codigo(ciudad.getCodigo())
                .latitud(ciudad.getLatitud())
                .longitud(ciudad.getLongitud())
                .build();
    }
}
