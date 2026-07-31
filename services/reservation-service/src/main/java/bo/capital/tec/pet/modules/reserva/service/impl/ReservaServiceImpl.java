package bo.capital.tec.pet.modules.reserva.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.entity.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaRechazadaEvent;
import bo.capital.tec.pet.modules.reserva.mapper.EstadoReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaCatalogMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_CANCELADA = "CANCELADA";

    private final ReservaMapper reservaMapper;
    private final EstadoReservaMapper estadoReservaMapper;
    private final ReservaCatalogMapper catalogMapper;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO dto) {
        if (reservaActiva(dto.getClienteId(), dto.getMascotaId(), dto.getServicioId(),
                dto.getFechaInicio(), dto.getFechaFin(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new BusinessException("Ya existe una reserva activa para esa mascota en el horario solicitado");
        }
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Long estadoId = estadoIdPorNombre(ESTADO_PENDIENTE);

        Reserva reserva = Reserva.builder()
                .codigo(codigo)
                .clienteId(dto.getClienteId())
                .proveedorId(dto.getProveedorId())
                .servicioId(dto.getServicioId())
                .mascotaId(dto.getMascotaId())
                .estadoReservaId(estadoId)
                .fechaReserva(dto.getFechaReserva())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .direccionReferencia(dto.getDireccionReferencia())
                .notas(dto.getNotas())
                .precioTotal(dto.getPrecioTotal())
                .build();
        reservaMapper.insert(reserva);
        ReservaResponseDTO response = toResponseDTO(reserva);
        publishCreada(reserva, response);
        return response;
    }

    private boolean reservaActiva(Long clienteId, Long mascotaId, Long servicioId,
                                  LocalDate fechaInicio, LocalDate fechaFin,
                                  java.time.LocalTime horaInicio, java.time.LocalTime horaFin) {
        List<Reserva> activas = reservaMapper.selectByClienteId(clienteId, 0, 100);
        return activas.stream()
                .filter(r -> r.getMascotaId().equals(mascotaId) && r.getServicioId().equals(servicioId))
                .filter(r -> r.getFechaInicio().equals(fechaInicio) || r.getFechaInicio().equals(fechaFin))
                .anyMatch(r -> horarioSolapado(r, horaInicio, horaFin));
    }

    private boolean horarioSolapado(Reserva existente, java.time.LocalTime inicio, java.time.LocalTime fin) {
        java.time.LocalTime finReal = fin != null ? fin : inicio.plusHours(1);
        java.time.LocalTime existenteFin = existente.getHoraFin() != null
                ? existente.getHoraFin()
                : existente.getHoraInicio().plusHours(1);
        return inicio.isBefore(existenteFin) && finReal.isAfter(existente.getHoraInicio());
    }

    private void publishCreada(Reserva reserva, ReservaResponseDTO response) {
        try {
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogMapper.selectCliente(reserva.getClienteId()) : null;
            eventPublisher.publish(new ReservaCreadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(),
                    cliente != null ? cliente.getEmail() : "",
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    reserva.getMascotaId(), response.getMascotaNombre(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal()));
        } catch (Exception e) {
            log.warn("Error publicando ReservaCreadaEvent: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO getById(Long id) {
        Reserva reserva = requireReserva(id);
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
                                                   String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                                   int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Reserva> reservas;
        long total;
        if (clienteId != null) {
            reservas = reservaMapper.selectByClienteId(clienteId, offset, size);
            total = reservaMapper.countByClienteId(clienteId);
        } else {
            reservas = reservaMapper.selectAll(offset, size);
            total = reservaMapper.countAll();
        }
        return buildPage(reservas, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservaSummaryDTO> getByClienteId(Long clienteId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Reserva> reservas = reservaMapper.selectByClienteId(clienteId, page * size, size);
        return buildPage(reservas, reservaMapper.countByClienteId(clienteId), page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservaSummaryDTO> getByProveedorId(Long proveedorId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Reserva> reservas = reservaMapper.selectByProveedorId(proveedorId, page * size, size);
        return buildPage(reservas, reservaMapper.countByProveedorId(proveedorId), page, size);
    }

    private PagedResponse<ReservaSummaryDTO> buildPage(List<Reserva> reservas, long total, int page, int size) {
        List<ReservaSummaryDTO> content = reservas.stream().map(this::toSummaryDTO).toList();
        return PagedResponse.<ReservaSummaryDTO>builder()
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
    public ReservaResponseDTO update(Long id, ReservaRequestDTO dto) {
        Reserva reserva = requireReserva(id);
        reserva.setClienteId(dto.getClienteId());
        reserva.setProveedorId(dto.getProveedorId());
        reserva.setServicioId(dto.getServicioId());
        reserva.setMascotaId(dto.getMascotaId());
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setFechaInicio(dto.getFechaInicio());
        reserva.setFechaFin(dto.getFechaFin());
        reserva.setHoraInicio(dto.getHoraInicio());
        reserva.setHoraFin(dto.getHoraFin());
        reserva.setLatitud(dto.getLatitud());
        reserva.setLongitud(dto.getLongitud());
        reserva.setDireccionReferencia(dto.getDireccionReferencia());
        reserva.setNotas(dto.getNotas());
        reserva.setPrecioTotal(dto.getPrecioTotal());
        reservaMapper.update(reserva);
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        requireReserva(id);
        reservaMapper.softDelete(id);
    }

    @Override
    @Transactional
    public ReservaResponseDTO cancelar(Long id, String motivo) {
        Reserva reserva = requireReserva(id);
        Long estadoId = estadoIdPorNombre(ESTADO_CANCELADA);
        reservaMapper.updateEstado(id, estadoId);
        reserva.setEstadoReservaId(estadoId);
        ReservaResponseDTO response = toResponseDTO(reserva);
        try {
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogMapper.selectCliente(reserva.getClienteId()) : null;
            eventPublisher.publish(new ReservaCanceladaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(),
                    cliente != null ? cliente.getEmail() : "",
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    motivo));
        } catch (Exception e) {
            log.warn("Error publicando ReservaCanceladaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO aplicarAceptacion(ReservaAceptadaEvent event) {
        Reserva reserva = requireReserva(event.getReservaId());
        Long estadoId = estadoIdPorNombre("CONFIRMADA");
        reservaMapper.updateRespuesta(reserva.getId(), event.getProveedorId(), estadoId, null);
        reserva.setProveedorId(event.getProveedorId());
        reserva.setEstadoReservaId(estadoId);
        reserva.setRespuestaEn(java.time.LocalDateTime.now());
        log.info("Reserva {} aceptada por proveedor {}", reserva.getCodigo(), event.getProveedorId());
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO aplicarRechazo(ReservaRechazadaEvent event) {
        Reserva reserva = requireReserva(event.getReservaId());
        Long estadoId = estadoIdPorNombre("RECHAZADA");
        reservaMapper.updateRespuesta(reserva.getId(), event.getProveedorId(), estadoId, event.getMotivoRechazo());
        reserva.setProveedorId(event.getProveedorId());
        reserva.setEstadoReservaId(estadoId);
        reserva.setMotivoRechazo(event.getMotivoRechazo());
        reserva.setRespuestaEn(java.time.LocalDateTime.now());
        log.info("Reserva {} rechazada por proveedor {}: {}", reserva.getCodigo(),
                event.getProveedorId(), event.getMotivoRechazo());
        return toResponseDTO(reserva);
    }

    private Long estadoIdPorNombre(String nombre) {
        EstadoReserva estado = estadoReservaMapper.selectByNombre(nombre);
        return estado != null ? estado.getId() : 1L;
    }

    private Reserva requireReserva(Long id) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        return reserva;
    }

    private ReservaResponseDTO toResponseDTO(Reserva reserva) {
        ClienteInfoDTO cliente = reserva.getClienteId() != null
                ? catalogMapper.selectCliente(reserva.getClienteId()) : null;
        ProveedorInfoDTO proveedor = reserva.getProveedorId() != null
                ? catalogMapper.selectProveedor(reserva.getProveedorId()) : null;
        ServicioInfoDTO servicio = catalogMapper.selectServicio(reserva.getServicioId());
        MascotaInfoDTO mascota = catalogMapper.selectMascota(reserva.getMascotaId());
        EstadoReserva estado = estadoReservaMapper.selectById(reserva.getEstadoReservaId());
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(cliente != null ? cliente.getNombre() : "")
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedor != null ? proveedor.getNombre() : "")
                .proveedorEmpresa(proveedor != null ? proveedor.getEmpresa() : "")
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicio != null ? servicio.getNombre() : "")
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascota != null ? mascota.getNombre() : "")
                .estadoReservaId(reserva.getEstadoReservaId())
                .estadoReservaNombre(estado != null ? estado.getNombre() : "")
                .estadoReservaColor(estado != null ? estado.getColor() : "")
                .fechaReserva(reserva.getFechaReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .latitud(reserva.getLatitud())
                .longitud(reserva.getLongitud())
                .direccionReferencia(reserva.getDireccionReferencia())
                .notas(reserva.getNotas())
                .precioTotal(reserva.getPrecioTotal())
                .motivoRechazo(reserva.getMotivoRechazo())
                .respuestaEn(reserva.getRespuestaEn())
                .createdAt(reserva.getCreatedAt())
                .build();
    }

    private ReservaSummaryDTO toSummaryDTO(Reserva reserva) {
        ClienteInfoDTO cliente = reserva.getClienteId() != null
                ? catalogMapper.selectCliente(reserva.getClienteId()) : null;
        ProveedorInfoDTO proveedor = reserva.getProveedorId() != null
                ? catalogMapper.selectProveedor(reserva.getProveedorId()) : null;
        ServicioInfoDTO servicio = catalogMapper.selectServicio(reserva.getServicioId());
        MascotaInfoDTO mascota = catalogMapper.selectMascota(reserva.getMascotaId());
        EstadoReserva estado = estadoReservaMapper.selectById(reserva.getEstadoReservaId());
        return ReservaSummaryDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(cliente != null ? cliente.getNombre() : "")
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedor != null ? proveedor.getNombre() : "")
                .proveedorEmpresa(proveedor != null ? proveedor.getEmpresa() : "")
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicio != null ? servicio.getNombre() : "")
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascota != null ? mascota.getNombre() : "")
                .estadoReservaId(reserva.getEstadoReservaId())
                .estadoReservaNombre(estado != null ? estado.getNombre() : "")
                .estadoReservaColor(estado != null ? estado.getColor() : "")
                .fechaReserva(reserva.getFechaReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .precioTotal(reserva.getPrecioTotal())
                .motivoRechazo(reserva.getMotivoRechazo())
                .build();
    }
}
