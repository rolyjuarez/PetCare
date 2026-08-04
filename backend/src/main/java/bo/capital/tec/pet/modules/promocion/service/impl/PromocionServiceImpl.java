package bo.capital.tec.pet.modules.promocion.service.impl;

import bo.capital.tec.pet.common.email.EmailService;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.cliente.mapper.ClienteMapper;
import bo.capital.tec.pet.modules.promocion.dto.PromocionRequestDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionResponseDTO;
import bo.capital.tec.pet.modules.promocion.dto.PromocionSummaryDTO;
import bo.capital.tec.pet.modules.promocion.entity.Promocion;
import bo.capital.tec.pet.modules.promocion.event.PromocionCreadaEvent;
import bo.capital.tec.pet.modules.promocion.mapper.PromocionMapper;
import bo.capital.tec.pet.modules.promocion.service.PromocionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PromocionServiceImpl implements PromocionService {

    private final PromocionMapper promocionMapper;
    private final ClienteMapper clienteMapper;
    private final EmailService emailService;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public PromocionResponseDTO create(PromocionRequestDTO dto) {
        validar(dto);
        Promocion promocion = buildFrom(dto, null);
        promocionMapper.insert(promocion);
        publicarPromocionCreada(promocion);
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional
    public PromocionResponseDTO createForProveedor(Long proveedorId, PromocionRequestDTO dto) {
        validar(dto);
        Promocion promocion = buildFrom(dto, proveedorId);
        promocionMapper.insert(promocion);
        publicarPromocionCreada(promocion);
        return toResponseDTO(promocion);
    }

    private void publicarPromocionCreada(Promocion promocion) {
        try {
            eventPublisher.publish(new PromocionCreadaEvent(
                    promocion.getId(), promocion.getCodigo(), promocion.getNombre(),
                    promocion.getDescripcion(), promocion.getTipoDescuento(),
                    promocion.getValorDescuento(), promocion.getFechaInicio(),
                    promocion.getFechaFin(), promocion.getProveedorId()));
        } catch (Exception e) {
            log.warn("Error publicando PromocionCreadaEvent: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PromocionResponseDTO getById(Long id) {
        Promocion promocion = promocionMapper.selectById(id);
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
        List<Promocion> promociones = promocionMapper.selectAll(offset, size);
        long total = promocionMapper.countAll();
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
    public PagedResponse<PromocionSummaryDTO> getByProveedor(Long proveedorId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Promocion> promociones = promocionMapper.selectByProveedorId(proveedorId, page * size, size);
        long total = promocionMapper.countByProveedorId(proveedorId);
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
        List<Promocion> promociones = promocionMapper.selectActive(0, 1000);
        return promociones.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromocionSummaryDTO> getActiveByDate(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay();
        List<Promocion> promociones = promocionMapper.selectActive(0, 1000);
        return promociones.stream()
                .filter(p -> p.getFechaInicio() != null && p.getFechaFin() != null)
                .filter(p -> !p.getFechaFin().isBefore(inicio) && !p.getFechaInicio().isAfter(fin))
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PromocionResponseDTO update(Long id, PromocionRequestDTO dto) {
        Promocion promocion = requirePromocion(id);
        validar(dto);
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
        promocionMapper.update(promocion);
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional
    public PromocionResponseDTO updateForProveedor(Long proveedorId, Long id, PromocionRequestDTO dto) {
        Promocion promocion = requireOwnedByProveedor(proveedorId, id);
        validar(dto);
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
        promocionMapper.update(promocion);
        return toResponseDTO(promocion);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requirePromocion(id);
        promocionMapper.softDelete(id);
    }

    @Override
    @Transactional
    public void deleteForProveedor(Long proveedorId, Long id) {
        requireOwnedByProveedor(proveedorId, id);
        promocionMapper.softDelete(id);
    }

    @Override
    @Transactional
    public int notificarClientes(Long promocionId) {
        Promocion promocion = requirePromocion(promocionId);
        List<String> emails = clienteMapper.selectAllEmails();
        if (emails == null || emails.isEmpty()) {
            log.info("Sin clientes con correo para notificar promoción {}", promocion.getCodigo());
            return 0;
        }
        String asunto = "Promocion " + promocion.getNombre() + " - PETCare";
        String cuerpo = "<h2>" + promocion.getNombre() + "</h2>"
                + "<p>" + (promocion.getDescripcion() != null ? promocion.getDescripcion() : "") + "</p>"
                + "<p>Usa el codigo <strong>" + promocion.getCodigo() + "</strong>"
                + " para obtener un descuento de " + promocion.getValorDescuento()
                + (Promocion.TIPO_PERCENTAGE.equals(promocion.getTipoDescuento()) ? "%" : " Bs") + ".</p>"
                + "<p>Vigencia: hasta " + (promocion.getFechaFin() != null ? promocion.getFechaFin().toLocalDate() : "") + ".</p>";
        int contador = 0;
        for (String email : emails) {
            if (email == null || email.isBlank()) {
                continue;
            }
            emailService.sendSimpleEmail(email, asunto, cuerpo);
            contador++;
        }
        log.info("Promoción {} notificada por correo a {} clientes", promocion.getCodigo(), contador);
        return contador;
    }

    private void validar(PromocionRequestDTO dto) {
        Promocion existing = promocionMapper.selectByCodigo(dto.getCodigo());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe una promocion con el codigo: " + dto.getCodigo());
        }
        if (dto.getFechaFin() != null && dto.getFechaInicio() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("La fecha de fin debe ser posterior a la de inicio");
        }
        if (Promocion.TIPO_PERCENTAGE.equals(dto.getTipoDescuento()) && dto.getValorDescuento().compareTo(java.math.BigDecimal.valueOf(100)) > 0) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("El porcentaje de descuento no puede superar 100");
        }
    }

    private Promocion buildFrom(PromocionRequestDTO dto, Long proveedorId) {
        return Promocion.builder()
                .proveedorId(proveedorId)
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
    }

    private Promocion requirePromocion(Long id) {
        Promocion promocion = promocionMapper.selectById(id);
        if (promocion == null) {
            throw new EntityNotFoundException("Promocion", id);
        }
        return promocion;
    }

    private Promocion requireOwnedByProveedor(Long proveedorId, Long id) {
        Promocion promocion = requirePromocion(id);
        if (!java.util.Objects.equals(promocion.getProveedorId(), proveedorId)) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("La promocion no pertenece al proveedor autenticado");
        }
        return promocion;
    }

    private PromocionResponseDTO toResponseDTO(Promocion promocion) {
        return PromocionResponseDTO.builder()
                .id(promocion.getId())
                .proveedorId(promocion.getProveedorId())
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
