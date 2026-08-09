package bo.capital.tec.pet.modules.promocion.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.entity.Promocion;
import bo.capital.tec.pet.modules.promocion.mapper.PromocionMapper;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {

    private final PromocionMapper promocionMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PromocionSummaryDTO> listar(Long proveedorId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Promocion> promociones =
                promocionMapper.selectByProveedorId(proveedorId, page * size, size);
        long total = promocionMapper.countByProveedorId(proveedorId);
        List<PromocionSummaryDTO> content = promociones.stream().map(this::toSummary).toList();
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
    public List<PromocionSummaryDTO> listarActivas(Long proveedorId) {
        return promocionMapper.selectActivasByProveedorId(proveedorId).stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional
    public PromocionResponseDTO crear(Long proveedorId, PromocionRequestDTO dto) {
        validar(dto);
        verificarCodigoUnico(dto.getCodigo(), null);
        verificarServicioDelProveedor(proveedorId, dto.getServicioId());
        Promocion promocion = Promocion.builder()
                .proveedorId(proveedorId)
                .servicioId(dto.getServicioId())
                .codigo(dto.getCodigo())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tipoDescuento(dto.getTipoDescuento())
                .valorDescuento(dto.getValorDescuento())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activa(Boolean.TRUE.equals(dto.getActiva()))
                .limiteUsos(dto.getLimiteUsos())
                .usosActuales(0)
                .build();
        promocionMapper.insert(promocion);
        log.info("Descuento {} creado por proveedor {}", promocion.getCodigo(), proveedorId);
        return toResponse(promocion);
    }

    @Override
    @Transactional
    public PromocionResponseDTO actualizar(Long proveedorId, Long id, PromocionRequestDTO dto) {
        Promocion existente = requirePromocion(id);
        verificarPertenencia(existente, proveedorId);
        validar(dto);
        verificarCodigoUnico(dto.getCodigo(), id);
        verificarServicioDelProveedor(proveedorId, dto.getServicioId());
        Promocion cambios = Promocion.builder()
                .id(id)
                .codigo(dto.getCodigo())
                .servicioId(dto.getServicioId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .tipoDescuento(dto.getTipoDescuento())
                .valorDescuento(dto.getValorDescuento())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .activa(Boolean.TRUE.equals(dto.getActiva()))
                .limiteUsos(dto.getLimiteUsos())
                .build();
        promocionMapper.update(cambios);
        log.info("Descuento {} actualizado por proveedor {}", id, proveedorId);
        return toResponse(promocionMapper.selectById(id));
    }

    @Override
    @Transactional
    public void eliminar(Long proveedorId, Long id) {
        Promocion promocion = requirePromocion(id);
        verificarPertenencia(promocion, proveedorId);
        promocionMapper.softDelete(id);
        log.info("Descuento {} eliminado por proveedor {}", id, proveedorId);
    }

    private Promocion requirePromocion(Long id) {
        Promocion promocion = promocionMapper.selectById(id);
        if (promocion == null || Boolean.TRUE.equals(promocion.getDeleted())) {
            throw new EntityNotFoundException("Promocion", id);
        }
        return promocion;
    }

    private void verificarPertenencia(Promocion promocion, Long proveedorId) {
        if (!promocion.getProveedorId().equals(proveedorId)) {
            throw new BusinessException("La promocion no pertenece a este proveedor");
        }
    }

    private void verificarCodigoUnico(String codigo, Long id) {
        Promocion existente = promocionMapper.selectByCodigo(codigo);
        if (existente != null && (id == null || !existente.getId().equals(id))) {
            throw new BusinessException("Ya existe una promocion con el codigo " + codigo);
        }
    }

    private void verificarServicioDelProveedor(Long proveedorId, Long servicioId) {
        if (servicioId != null && !promocionMapper.existeServicioDelProveedor(proveedorId, servicioId)) {
            throw new BusinessException("El servicio " + servicioId + " no pertenece al proveedor " + proveedorId);
        }
    }

    private void validar(PromocionRequestDTO dto) {
        if (!dto.getFechaFin().isAfter(dto.getFechaInicio())) {
            throw new BusinessException("La fecha de fin debe ser posterior a la fecha de inicio");
        }
        if (Promocion.TIPO_PERCENTAGE.equals(dto.getTipoDescuento())
                && dto.getValorDescuento().compareTo(new java.math.BigDecimal("100")) > 0) {
            throw new BusinessException("Un descuento porcentual no puede exceder 100%");
        }
    }

    private PromocionSummaryDTO toSummary(Promocion p) {
        return PromocionSummaryDTO.builder()
                .id(p.getId())
                .proveedorId(p.getProveedorId())
                .servicioId(p.getServicioId())
                .servicioNombre(p.getServicioNombre())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .tipoDescuento(p.getTipoDescuento())
                .valorDescuento(p.getValorDescuento())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activa(p.getActiva())
                .build();
    }

    private PromocionResponseDTO toResponse(Promocion p) {
        return PromocionResponseDTO.builder()
                .id(p.getId())
                .proveedorId(p.getProveedorId())
                .servicioId(p.getServicioId())
                .servicioNombre(p.getServicioNombre())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .tipoDescuento(p.getTipoDescuento())
                .valorDescuento(p.getValorDescuento())
                .fechaInicio(p.getFechaInicio())
                .fechaFin(p.getFechaFin())
                .activa(p.getActiva())
                .limiteUsos(p.getLimiteUsos())
                .usosActuales(p.getUsosActuales())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
