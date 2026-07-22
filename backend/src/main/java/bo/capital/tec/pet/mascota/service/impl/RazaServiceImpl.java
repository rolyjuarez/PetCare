package bo.capital.tec.pet.mascota.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.mascota.dto.RazaRequestDTO;
import bo.capital.tec.pet.mascota.dto.RazaResponseDTO;
import bo.capital.tec.pet.mascota.dto.RazaSummaryDTO;
import bo.capital.tec.pet.mascota.entity.Especie;
import bo.capital.tec.pet.mascota.entity.Raza;
import bo.capital.tec.pet.mascota.mapper.EspecieMapper;
import bo.capital.tec.pet.mascota.mapper.RazaMapper;
import bo.capital.tec.pet.mascota.service.RazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RazaServiceImpl implements RazaService {

    private final RazaMapper razaMapper;
    private final EspecieMapper especieMapper;

    @Override
    @Transactional
    public RazaResponseDTO create(RazaRequestDTO dto) {
        Raza raza = Raza.builder()
                .nombre(dto.getNombre())
                .especieId(dto.getEspecieId())
                .build();
        razaMapper.insert(raza);
        return toResponseDTO(raza);
    }

    @Override
    @Transactional(readOnly = true)
    public RazaResponseDTO getById(Long id) {
        Raza raza = razaMapper.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        return toResponseDTO(raza);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaSummaryDTO> getByEspecieId(Long especieId) {
        List<Raza> razas = razaMapper.selectByEspecieId(especieId);
        return razas.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RazaSummaryDTO> getAll(int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Raza> razas = razaMapper.selectAll(offset, size);
        long total = razaMapper.countAll();
        List<RazaSummaryDTO> content = razas.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<RazaSummaryDTO>builder()
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
    public RazaResponseDTO update(Long id, RazaRequestDTO dto) {
        Raza raza = razaMapper.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        raza.setNombre(dto.getNombre());
        raza.setEspecieId(dto.getEspecieId());
        razaMapper.update(raza);
        return toResponseDTO(raza);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Raza raza = razaMapper.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        razaMapper.softDelete(id);
    }

    private RazaResponseDTO toResponseDTO(Raza raza) {
        String especieNombre = "";
        if (raza.getEspecieId() != null) {
            Especie especie = especieMapper.selectById(raza.getEspecieId());
            if (especie != null) {
                especieNombre = especie.getNombre();
            }
        }
        return RazaResponseDTO.builder()
                .id(raza.getId())
                .nombre(raza.getNombre())
                .especieId(raza.getEspecieId())
                .especieNombre(especieNombre)
                .createdAt(raza.getCreatedAt())
                .build();
    }

    private RazaSummaryDTO toSummaryDTO(Raza raza) {
        String especieNombre = "";
        if (raza.getEspecieId() != null) {
            Especie especie = especieMapper.selectById(raza.getEspecieId());
            if (especie != null) {
                especieNombre = especie.getNombre();
            }
        }
        return RazaSummaryDTO.builder()
                .id(raza.getId())
                .nombre(raza.getNombre())
                .especieNombre(especieNombre)
                .build();
    }
}
