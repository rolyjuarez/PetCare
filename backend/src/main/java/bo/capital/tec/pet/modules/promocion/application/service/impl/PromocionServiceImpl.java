package bo.capital.tec.pet.modules.promocion.application.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.domain.model.Promocion;
import bo.capital.tec.pet.modules.promocion.domain.port.PromocionRepository;
import bo.capital.tec.pet.modules.promocion.application.service.PromocionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;

    @Override
    @Transactional
    public PromocionResponseDTO create(PromocionRequestDTO dto) {
        Promocion existing = promocionRepository.selectByCodigo(dto.getCodigo());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe una promocion con el codigo: " + dto.getCodigo());
        }
        Promocion promocion = Promocion.builder()
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tipoDescuento(dto.getTipoDescuento())
                .valorDescuento(dto.getValorDescuento())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .limiteUsos(dto.getLimiteUsos())
                .usosActuales(0)
                .build();
        promocionRepository.insert(promocion);
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionResponseDTO getById(Long id) {
        Promocion promocion = promocionRepository.selectById(id);
        if (promocion == null) {
            throw new EntityNotFoundException("Promocion", id);
        }
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PromocionSummaryDTO> getAll(String nombre, boolean activo, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Promocion> promociones = promocionRepository.selectAll(offset, size);
        long total = promocionRepository.countAll();
        List<PromocionSummaryDTO> content = promociones.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<PromocionSummaryDTO>builder()
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
    public List<PromocionSummaryDTO> getActive() {
        List<Promocion> promociones = promocionRepository.selectActive(0, 1000);
        return promociones.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionSummaryDTO> getActiveByDate(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        List<Promocion> promociones = promocionRepository.selectActive(0, 1000);
        return promociones.stream()
                .filter(p -> p.getFechaInicio() != null && p.getFechaFin() != null)
                .filter(p -> !p.getFechaFin().isBefore(inicio) && !p.getFechaInicio().isAfter(fin))
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromocionResponseDTO update(Long id, PromocionRequestDTO dto) {
        Promocion promocion = promocionRepository.selectById(id);
        if (promocion == null) {
            throw new EntityNotFoundException("Promocion", id);
        }
        promocion.setCodigo(dto.getCodigo());
        promocion.setNombre(dto.getNombre());
        promocion.setDescripcion(dto.getDescripcion());
        promocion.setTipoDescuento(dto.getTipoDescuento());
        promocion.setValorDescuento(dto.getValorDescuento());
        promocion.setFechaInicio(dto.getFechaInicio());
        promocion.setFechaFin(dto.getFechaFin());
        if (dto.getActiva() != null) {
            promocion.setActiva(dto.getActiva());
        }
        promocion.setLimiteUsos(dto.getLimiteUsos());
        promocionRepository.update(promocion);
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Promocion promocion = promocionRepository.selectById(id);
        if (promocion == null) {
            throw new EntityNotFoundException("Promocion", id);
        }
        promocionRepository.softDelete(id);
    }

    private PromocionResponseDTO toResponseDTO(Promocion promocion) {
        return PromocionResponseDTO.builder()
                .id(promocion.getId())
                .codigo(promocion.getCodigo())
                .nombre(promocion.getNombre())
                .descripcion(promocion.getDescripcion())
                .tipoDescuento(promocion.getTipoDescuento())
                .valorDescuento(promocion.getValorDescuento())
                .fechaInicio(promocion.getFechaInicio())
                .fechaFin(promocion.getFechaFin())
                .activa(promocion.getActiva())
                .limiteUsos(promocion.getLimiteUsos())
                .usosActuales(promocion.getUsosActuales())
                .createdAt(promocion.getCreatedAt())
                .build();
    }

    private PromocionSummaryDTO toSummaryDTO(Promocion promocion) {
        return PromocionSummaryDTO.builder()
                .id(promocion.getId())
                .codigo(promocion.getCodigo())
                .nombre(promocion.getNombre())
                .tipoDescuento(promocion.getTipoDescuento())
                .valorDescuento(promocion.getValorDescuento())
                .fechaFin(promocion.getFechaFin())
                .activa(promocion.getActiva())
                .build();
    }
}
