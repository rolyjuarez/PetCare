package bo.capital.tec.pet.modules.vacuna.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.vacuna.dto.VacunaRequestDTO;
import bo.capital.tec.pet.modules.vacuna.dto.VacunaResponseDTO;
import bo.capital.tec.pet.modules.vacuna.dto.VacunaSummaryDTO;
import bo.capital.tec.pet.modules.vacuna.entity.Vacuna;
import bo.capital.tec.pet.modules.vacuna.mapper.VacunaMapper;
import bo.capital.tec.pet.modules.vacuna.service.VacunaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacunaServiceImpl implements VacunaService {

    private final VacunaMapper vacunaMapper;

    @Override
    @Transactional
    public VacunaResponseDTO create(VacunaRequestDTO dto) {
        Vacuna vacuna = Vacuna.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .periodicidadMeses(dto.getPeriodicidadMeses())
                .build();
        vacunaMapper.insert(vacuna);
        return toResponseDTO(vacuna);
    }

    @Override
    @Transactional(readOnly = true)
    public VacunaResponseDTO getById(Long id) {
        Vacuna vacuna = vacunaMapper.selectById(id);
        if (vacuna == null) {
            throw new EntityNotFoundException("Vacuna", id);
        }
        return toResponseDTO(vacuna);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VacunaSummaryDTO> getAll(String nombre, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Vacuna> vacunas = vacunaMapper.selectAll(offset, size);
        long total = vacunaMapper.countAll();
        List<VacunaSummaryDTO> content = vacunas.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<VacunaSummaryDTO>builder()
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
    public VacunaResponseDTO update(Long id, VacunaRequestDTO dto) {
        Vacuna vacuna = vacunaMapper.selectById(id);
        if (vacuna == null) {
            throw new EntityNotFoundException("Vacuna", id);
        }
        vacuna.setNombre(dto.getNombre());
        vacuna.setDescripcion(dto.getDescripcion());
        vacuna.setPeriodicidadMeses(dto.getPeriodicidadMeses());
        vacunaMapper.update(vacuna);
        return toResponseDTO(vacuna);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vacuna vacuna = vacunaMapper.selectById(id);
        if (vacuna == null) {
            throw new EntityNotFoundException("Vacuna", id);
        }
        vacunaMapper.softDelete(id);
    }

    private VacunaResponseDTO toResponseDTO(Vacuna vacuna) {
        return VacunaResponseDTO.builder()
                .id(vacuna.getId())
                .nombre(vacuna.getNombre())
                .descripcion(vacuna.getDescripcion())
                .periodicidadMeses(vacuna.getPeriodicidadMeses())
                .createdAt(vacuna.getCreatedAt())
                .build();
    }

    private VacunaSummaryDTO toSummaryDTO(Vacuna vacuna) {
        return VacunaSummaryDTO.builder()
                .id(vacuna.getId())
                .nombre(vacuna.getNombre())
                .periodicidadMeses(vacuna.getPeriodicidadMeses())
                .build();
    }
}
