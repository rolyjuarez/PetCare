package bo.capital.tec.pet.modules.vacuna.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.api.MascotaApi;
import bo.capital.tec.pet.modules.mascota.domain.model.Mascota;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionRequestDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionResponseDTO;
import bo.capital.tec.pet.modules.vacuna.dto.RegistroVacunacionSummaryDTO;
import bo.capital.tec.pet.modules.vacuna.domain.model.RegistroVacunacion;
import bo.capital.tec.pet.modules.vacuna.domain.model.Vacuna;
import bo.capital.tec.pet.modules.vacuna.domain.port.RegistroVacunacionRepository;
import bo.capital.tec.pet.modules.vacuna.domain.port.VacunaRepository;
import bo.capital.tec.pet.modules.vacuna.application.service.RegistroVacunacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistroVacunacionServiceImpl implements RegistroVacunacionService {

    private final RegistroVacunacionRepository registroVacunacionRepository;
    private final VacunaRepository vacunaRepository;
    private final MascotaApi mascotaApi;

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
        registroVacunacionRepository.insert(rv);
        return toResponseDTO(rv);
    }

    @Override
    @Transactional(readOnly = true)
    public RegistroVacunacionResponseDTO getById(Long id) {
        RegistroVacunacion rv = registroVacunacionRepository.selectById(id);
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
            registros = registroVacunacionRepository.selectByMascotaId(mascotaId, offset, size);
            total = registroVacunacionRepository.countByMascotaId(mascotaId);
        } else {
            registros = registroVacunacionRepository.selectAll(offset, size);
            total = registroVacunacionRepository.countAll();
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
        RegistroVacunacion rv = registroVacunacionRepository.selectById(id);
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
        registroVacunacionRepository.update(rv);
        return toResponseDTO(rv);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RegistroVacunacion rv = registroVacunacionRepository.selectById(id);
        if (rv == null) {
            throw new EntityNotFoundException("RegistroVacunacion", id);
        }
        registroVacunacionRepository.softDelete(id);
    }

    private RegistroVacunacionResponseDTO toResponseDTO(RegistroVacunacion rv) {
        String mascotaNombre = "";
        if (rv.getMascotaId() != null) {
            Mascota mascota = mascotaApi.selectById(rv.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String vacunaNombre = "";
        if (rv.getVacunaId() != null) {
            Vacuna vacuna = vacunaRepository.selectById(rv.getVacunaId());
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
            Mascota mascota = mascotaApi.selectById(rv.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String vacunaNombre = "";
        if (rv.getVacunaId() != null) {
            Vacuna vacuna = vacunaRepository.selectById(rv.getVacunaId());
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
