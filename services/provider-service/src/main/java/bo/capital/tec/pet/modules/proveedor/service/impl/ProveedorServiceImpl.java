package bo.capital.tec.pet.modules.proveedor.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.proveedor.dto.ResponderSolicitudRequestDTO;
import bo.capital.tec.pet.modules.proveedor.dto.SolicitudReservaResponseDTO;
import bo.capital.tec.pet.modules.proveedor.entity.SolicitudReserva;
import bo.capital.tec.pet.modules.proveedor.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.proveedor.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.proveedor.event.ReservaRechazadaEvent;
import bo.capital.tec.pet.modules.proveedor.mapper.ProveedorCatalogMapper;
import bo.capital.tec.pet.modules.proveedor.mapper.SolicitudReservaMapper;
import bo.capital.tec.pet.modules.proveedor.mapper.VacunaCatalogMapper;
import bo.capital.tec.pet.modules.proveedor.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_ACEPTADA = "ACEPTADA";
    private static final String ESTADO_RECHAZADA = "RECHAZADA";

    private final SolicitudReservaMapper solicitudReservaMapper;
    private final ProveedorCatalogMapper proveedorCatalogMapper;
    private final VacunaCatalogMapper vacunaCatalogMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SolicitudReservaResponseDTO> listarSolicitudes(Long proveedorId, String estado,
                                                                        int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<SolicitudReserva> solicitudes =
                solicitudReservaMapper.selectByProveedor(proveedorId, estado, page * size, size);
        long total = solicitudReservaMapper.countByProveedor(proveedorId, estado);
        List<SolicitudReservaResponseDTO> content = solicitudes.stream().map(this::toDTO).toList();
        return PagedResponse.<SolicitudReservaResponseDTO>builder()
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
    public SolicitudReservaResponseDTO aceptar(Long proveedorId, Long solicitudId,
                                               ResponderSolicitudRequestDTO request) {
        SolicitudReserva solicitud = requireSolicitud(solicitudId);
        verificarPertenencia(solicitud, proveedorId);
        verificarPendiente(solicitud);
        String comentario = request != null ? request.getComentario() : null;
        solicitudReservaMapper.updateEstado(solicitudId, ESTADO_ACEPTADA, null);
        solicitud.setEstado(ESTADO_ACEPTADA);
        solicitud.setRespondidaEn(java.time.LocalDateTime.now());
        publishAceptada(solicitud, comentario);
        log.info("Solicitud {} aceptada por proveedor {}", solicitud.getCodigo(), proveedorId);
        return toDTO(solicitud);
    }

    @Override
    @Transactional
    public SolicitudReservaResponseDTO rechazar(Long proveedorId, Long solicitudId,
                                                ResponderSolicitudRequestDTO request) {
        SolicitudReserva solicitud = requireSolicitud(solicitudId);
        verificarPertenencia(solicitud, proveedorId);
        verificarPendiente(solicitud);
        String motivo = request != null ? request.getMotivoRechazo() : null;
        solicitudReservaMapper.updateEstado(solicitudId, ESTADO_RECHAZADA, motivo);
        solicitud.setEstado(ESTADO_RECHAZADA);
        solicitud.setMotivoRechazo(motivo);
        solicitud.setRespondidaEn(java.time.LocalDateTime.now());
        publishRechazada(solicitud, motivo);
        log.info("Solicitud {} rechazada por proveedor {}: {}", solicitud.getCodigo(), proveedorId, motivo);
        return toDTO(solicitud);
    }

    @Override
    @Transactional
    public void procesarReservaCreada(ReservaCreadaEvent event) {
        List<Long> proveedorIds;
        if (event.getProveedorId() != null) {
            proveedorIds = List.of(event.getProveedorId());
        } else {
            proveedorIds = proveedorCatalogMapper.selectProveedoresByServicioId(event.getServicioId());
        }
        if (proveedorIds.isEmpty()) {
            log.warn("Sin proveedores para el servicio {} de la reserva {}",
                    event.getServicioId(), event.getReservaId());
            return;
        }
        for (Long proveedorId : proveedorIds) {
            if (solicitudReservaMapper.existsByReservaYProveedor(event.getReservaId(), proveedorId)) {
                log.debug("Solicitud ya creada para reserva {} y proveedor {}, ignorada",
                        event.getReservaId(), proveedorId);
                continue;
            }
            SolicitudReserva solicitud = SolicitudReserva.builder()
                    .reservaId(event.getReservaId())
                    .codigo(event.getCodigo())
                    .clienteId(event.getClienteId())
                    .clienteNombre(event.getClienteNombre())
                    .proveedorId(proveedorId)
                    .proveedorEmpresa(event.getProveedorEmpresa())
                    .servicioId(event.getServicioId())
                    .servicioNombre(event.getServicioNombre())
                    .mascotaId(event.getMascotaId())
                    .mascotaNombre(event.getMascotaNombre())
                    .fechaInicio(event.getFechaInicio())
                    .horaInicio(event.getHoraInicio())
                    .precioTotal(event.getPrecioTotal())
                    .modalidadEntrega(event.getModalidadEntrega())
                    .registroVacunacionId(event.getRegistroVacunacionId())
                    .estado(ESTADO_PENDIENTE)
                    .build();
            solicitudReservaMapper.insert(solicitud);
            log.info("Solicitud {} creada para proveedor {}", solicitud.getCodigo(), proveedorId);
        }
    }

    private void publishAceptada(SolicitudReserva solicitud, String comentario) {
        eventPublisher.publish(new ReservaAceptadaEvent(
                solicitud.getReservaId(), solicitud.getCodigo(),
                solicitud.getProveedorId(), null, solicitud.getProveedorEmpresa(),
                solicitud.getServicioNombre(), Instant.now(), comentario));
    }

    private void publishRechazada(SolicitudReserva solicitud, String motivo) {
        eventPublisher.publish(new ReservaRechazadaEvent(
                solicitud.getReservaId(), solicitud.getCodigo(),
                solicitud.getProveedorId(), null, solicitud.getProveedorEmpresa(),
                solicitud.getServicioNombre(), motivo, Instant.now()));
    }

    private SolicitudReserva requireSolicitud(Long solicitudId) {
        SolicitudReserva solicitud = solicitudReservaMapper.selectById(solicitudId);
        if (solicitud == null) {
            throw new EntityNotFoundException("SolicitudReserva", solicitudId);
        }
        return solicitud;
    }

    private void verificarPertenencia(SolicitudReserva solicitud, Long proveedorId) {
        if (!solicitud.getProveedorId().equals(proveedorId)) {
            throw new BusinessException("La solicitud no pertenece a este proveedor");
        }
    }

    private void verificarPendiente(SolicitudReserva solicitud) {
        if (!ESTADO_PENDIENTE.equals(solicitud.getEstado())) {
            throw new BusinessException("La solicitud ya fue respondida");
        }
    }

    private SolicitudReservaResponseDTO toDTO(SolicitudReserva s) {
        bo.capital.tec.pet.modules.proveedor.dto.VacunaInfoDTO vacunaInfo =
                s.getRegistroVacunacionId() != null
                        ? vacunaCatalogMapper.selectRegistroVacunacion(s.getRegistroVacunacionId()) : null;
        return SolicitudReservaResponseDTO.builder()
                .id(s.getId())
                .reservaId(s.getReservaId())
                .codigo(s.getCodigo())
                .clienteId(s.getClienteId())
                .clienteNombre(s.getClienteNombre())
                .proveedorId(s.getProveedorId())
                .proveedorEmpresa(s.getProveedorEmpresa())
                .servicioId(s.getServicioId())
                .servicioNombre(s.getServicioNombre())
                .mascotaId(s.getMascotaId())
                .mascotaNombre(s.getMascotaNombre())
                .fechaInicio(s.getFechaInicio())
                .horaInicio(s.getHoraInicio())
                .precioTotal(s.getPrecioTotal())
                .modalidadEntrega(s.getModalidadEntrega())
                .latitud(s.getLatitud())
                .longitud(s.getLongitud())
                .direccionReferencia(s.getDireccionReferencia())
                .estado(s.getEstado())
                .motivoRechazo(s.getMotivoRechazo())
                .registroVacunacionId(s.getRegistroVacunacionId())
                .vacunaInfo(vacunaInfo)
                .creadaEn(s.getCreadaEn())
                .respondidaEn(s.getRespondidaEn())
                .build();
    }
}
