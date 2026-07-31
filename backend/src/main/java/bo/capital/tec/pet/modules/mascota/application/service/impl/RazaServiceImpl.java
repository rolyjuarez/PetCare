package bo.capital.tec.pet.modules.mascota.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.dto.RazaRequestDTO;
import bo.capital.tec.pet.modules.mascota.dto.RazaResponseDTO;
import bo.capital.tec.pet.modules.mascota.dto.RazaSummaryDTO;
import bo.capital.tec.pet.modules.mascota.domain.model.Especie;
import bo.capital.tec.pet.modules.mascota.domain.model.Raza;
import bo.capital.tec.pet.modules.mascota.domain.port.EspecieRepository;
import bo.capital.tec.pet.modules.mascota.domain.port.RazaRepository;
import bo.capital.tec.pet.modules.mascota.application.service.RazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RazaServiceImpl implements RazaService {

    private final RazaRepository razaRepository;
    private final EspecieRepository especieRepository;

    @Override
    @Transactional
    public RazaResponseDTO create(RazaRequestDTO dto) {
        Raza raza = Raza.builder()
                .nombre(dto.getNombre())
                .especieId(dto.getEspecieId())
                .build();
        razaRepository.insert(raza);
        return toResponseDTO(raza);
    }

    @Override
    @Transactional(readOnly = true)
    public RazaResponseDTO getById(Long id) {
        Raza raza = razaRepository.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        return toResponseDTO(raza);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RazaSummaryDTO> getByEspecieId(Long especieId) {
        List<Raza> razas = razaRepository.selectByEspecieId(especieId);
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
        List<Raza> razas = razaRepository.selectAll(offset, size);
        long total = razaRepository.countAll();
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
        Raza raza = razaRepository.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        raza.setNombre(dto.getNombre());
        raza.setEspecieId(dto.getEspecieId());
        razaRepository.update(raza);
        return toResponseDTO(raza);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Raza raza = razaRepository.selectById(id);
        if (raza == null) {
            throw new EntityNotFoundException("Raza", id);
        }
        razaRepository.softDelete(id);
    }

    private RazaResponseDTO toResponseDTO(Raza raza) {
        String especieNombre = "";
        if (raza.getEspecieId() != null) {
            Especie especie = especieRepository.selectById(raza.getEspecieId());
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
            Especie especie = especieRepository.selectById(raza.getEspecieId());
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
