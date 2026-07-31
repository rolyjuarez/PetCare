package bo.capital.tec.pet.modules.mascota.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.dto.EspecieRequestDTO;
import bo.capital.tec.pet.modules.mascota.dto.EspecieResponseDTO;
import bo.capital.tec.pet.modules.mascota.dto.EspecieSummaryDTO;
import bo.capital.tec.pet.modules.mascota.domain.model.Especie;
import bo.capital.tec.pet.modules.mascota.domain.port.EspecieRepository;
import bo.capital.tec.pet.modules.mascota.application.service.EspecieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspecieServiceImpl implements EspecieService {

    private final EspecieRepository especieRepository;

    @Override
    @Transactional
    public EspecieResponseDTO create(EspecieRequestDTO dto) {
        Especie especie = Especie.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();
        especieRepository.insert(especie);
        return toResponseDTO(especie);
    }

    @Override
    @Transactional(readOnly = true)
    public EspecieResponseDTO getById(Long id) {
        Especie especie = especieRepository.selectById(id);
        if (especie == null) {
            throw new EntityNotFoundException("Especie", id);
        }
        return toResponseDTO(especie);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EspecieSummaryDTO> getAll(int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Especie> especies = especieRepository.selectAll(offset, size);
        long total = especieRepository.countAll();
        List<EspecieSummaryDTO> content = especies.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<EspecieSummaryDTO>builder()
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
    public EspecieResponseDTO update(Long id, EspecieRequestDTO dto) {
        Especie especie = especieRepository.selectById(id);
        if (especie == null) {
            throw new EntityNotFoundException("Especie", id);
        }
        especie.setNombre(dto.getNombre());
        especie.setDescripcion(dto.getDescripcion());
        especieRepository.update(especie);
        return toResponseDTO(especie);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Especie especie = especieRepository.selectById(id);
        if (especie == null) {
            throw new EntityNotFoundException("Especie", id);
        }
        especieRepository.softDelete(id);
    }

    private EspecieResponseDTO toResponseDTO(Especie especie) {
        return EspecieResponseDTO.builder()
                .id(especie.getId())
                .nombre(especie.getNombre())
                .descripcion(especie.getDescripcion())
                .createdAt(especie.getCreatedAt())
                .build();
    }

    private EspecieSummaryDTO toSummaryDTO(Especie especie) {
        return EspecieSummaryDTO.builder()
                .id(especie.getId())
                .nombre(especie.getNombre())
                .build();
    }
}
