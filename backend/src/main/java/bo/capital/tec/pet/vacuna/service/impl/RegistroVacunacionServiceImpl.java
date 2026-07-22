package bo.capital.tec.pet.vacuna.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.mascota.entity.Mascota;
import bo.capital.tec.pet.mascota.mapper.MascotaMapper;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.vacuna.dto.RegistroVacunacionSummaryDTO;
import bo.capital.tec.pet.vacuna.entity.RegistroVacunacion;
import bo.capital.tec.pet.vacuna.entity.Vacuna;
import bo.capital.tec.pet.vacuna.mapper.RegistroVacunacionMapper;
import bo.capital.tec.pet.vacuna.mapper.VacunaMapper;
import bo.capital.tec.pet.vacuna.service.RegistroVacunacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroVacunacionServiceImpl implements RegistroVacunacionService {

    private final RegistroVacunacionMapper registroVacunacionMapper;
    private final VacunaMapper vacunaMapper;
    private final MascotaMapper mascotaMapper;

    @Override
    @Transactional
    public RegistroVacunacionResponseDTO create(RegistroVacunacionRequestDTO dto) {
        RegistroVacunacion rv = RegistroVacunacion.builder()
                .mascotaId(dto.getMascotaId())
                .vacunaId(dto.getVacunaId())
                .fechaAplicacion(dto.getFechaAplicacion())
                .fechaVencimiento(dto.getFechaVencimiento())
                .lote(dto.getLote())
                .veterinario(dto.getVeterinario())
                .observaciones(dto.getObservaciones())
                .build();
        registroVacunacionMapper.insert(rv);
        return toResponseDTO(rv);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroVacunacionResponseDTO getById(Long id) {
        RegistroVacunacion rv = registroVacunacionMapper.selectById(id);
        if (rv == null) {
            throw new EntityNotFoundException("RegistroVacunacion", id);
        }
        return toResponseDTO(rv);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RegistroVacunacionSummaryDTO> getAll(Long mascotaId, Long vacunaId,
            LocalDate fechaDesde, LocalDate fechaHasta, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<RegistroVacunacion> registros;
        long total;
        if (mascotaId != null) {
            registros = registroVacunacionMapper.selectByMascotaId(mascotaId, offset, size);
            total = registroVacunacionMapper.countByMascotaId(mascotaId);
        } else {
            registros = registroVacunacionMapper.selectAll(offset, size);
            total = registroVacunacionMapper.countAll();
        }
        List<RegistroVacunacionSummaryDTO> content = registros.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<RegistroVacunacionSummaryDTO>builder()
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
    public RegistroVacunacionResponseDTO update(Long id, RegistroVacunacionRequestDTO dto) {
        RegistroVacunacion rv = registroVacunacionMapper.selectById(id);
        if (rv == null) {
            throw new EntityNotFoundException("RegistroVacunacion", id);
        }
        rv.setMascotaId(dto.getMascotaId());
        rv.setVacunaId(dto.getVacunaId());
        rv.setFechaAplicacion(dto.getFechaAplicacion());
        rv.setFechaVencimiento(dto.getFechaVencimiento());
        rv.setLote(dto.getLote());
        rv.setVeterinario(dto.getVeterinario());
        rv.setObservaciones(dto.getObservaciones());
        registroVacunacionMapper.update(rv);
        return toResponseDTO(rv);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RegistroVacunacion rv = registroVacunacionMapper.selectById(id);
        if (rv == null) {
            throw new EntityNotFoundException("RegistroVacunacion", id);
        }
        registroVacunacionMapper.softDelete(id);
    }

    private RegistroVacunacionResponseDTO toResponseDTO(RegistroVacunacion rv) {
        String mascotaNombre = "";
        if (rv.getMascotaId() != null) {
            Mascota mascota = mascotaMapper.selectById(rv.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String vacunaNombre = "";
        if (rv.getVacunaId() != null) {
            Vacuna vacuna = vacunaMapper.selectById(rv.getVacunaId());
            if (vacuna != null) {
                vacunaNombre = vacuna.getNombre();
            }
        }
        return RegistroVacunacionResponseDTO.builder()
                .id(rv.getId())
                .mascotaId(rv.getMascotaId())
                .mascotaNombre(mascotaNombre)
                .vacunaId(rv.getVacunaId())
                .vacunaNombre(vacunaNombre)
                .fechaAplicacion(rv.getFechaAplicacion())
                .fechaVencimiento(rv.getFechaVencimiento())
                .lote(rv.getLote())
                .veterinario(rv.getVeterinario())
                .observaciones(rv.getObservaciones())
                .createdAt(rv.getCreatedAt())
                .build();
    }

    private RegistroVacunacionSummaryDTO toSummaryDTO(RegistroVacunacion rv) {
        String mascotaNombre = "";
        if (rv.getMascotaId() != null) {
            Mascota mascota = mascotaMapper.selectById(rv.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String vacunaNombre = "";
        if (rv.getVacunaId() != null) {
            Vacuna vacuna = vacunaMapper.selectById(rv.getVacunaId());
            if (vacuna != null) {
                vacunaNombre = vacuna.getNombre();
            }
        }
        return RegistroVacunacionSummaryDTO.builder()
                .id(rv.getId())
                .mascotaId(rv.getMascotaId())
                .mascotaNombre(mascotaNombre)
                .vacunaId(rv.getVacunaId())
                .vacunaNombre(vacunaNombre)
                .fechaAplicacion(rv.getFechaAplicacion())
                .fechaVencimiento(rv.getFechaVencimiento())
                .build();
    }
}
