package bo.capital.tec.pet.modules.reserva.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.email.EmailService;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import bo.capital.tec.pet.modules.notificacion.mapper.NotificacionMapper;
import bo.capital.tec.pet.modules.reserva.dto.ClienteInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.DisponibilidadSlotsDTO;
import bo.capital.tec.pet.modules.reserva.dto.MascotaInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ModalidadInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ProveedorInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.dto.RegistroVacunacionInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.ServicioInfoDTO;
import bo.capital.tec.pet.modules.reserva.dto.SlotDTO;
import bo.capital.tec.pet.modules.reserva.entity.Disponibilidad;
import bo.capital.tec.pet.modules.reserva.entity.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.event.ReservaAceptadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaRechazadaEvent;
import bo.capital.tec.pet.modules.reserva.mapper.DisponibilidadMapper;
import bo.capital.tec.pet.modules.reserva.mapper.EstadoReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaCatalogMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.VacunaCatalogMapper;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final DisponibilidadMapper disponibilidadMapper;
    private final VacunaCatalogMapper vacunaCatalogMapper;
    private final DomainEventPublisher eventPublisher;
    private final NotificacionMapper notificacionMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO dto) {
        if (reservaActiva(dto.getClienteId(), dto.getMascotaId(), dto.getServicioId(),
                dto.getFechaInicio(), dto.getFechaFin(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new BusinessException("Ya existe una reserva activa para esa mascota en el horario solicitado");
        }
        validarSlotProveedor(dto);
        validarCertificado(dto);
        String modalidad = validarModalidad(dto);
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Long estadoId = estadoIdPorNombre(ESTADO_PENDIENTE);

        Reserva reserva = Reserva.builder()
                .codigo(codigo)
                .clienteId(dto.getClienteId())
                .proveedorId(dto.getProveedorId())
                .servicioId(dto.getServicioId())
                .mascotaId(dto.getMascotaId())
                .estadoReservaId(estadoId)
                .registroVacunacionId(dto.getRegistroVacunacionId())
                .modalidadEntrega(modalidad)
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
                    reserva.getPrecioTotal(), reserva.getRegistroVacunacionId(),
                    reserva.getModalidadEntrega()));
        } catch (Exception e) {
            log.warn("Error publicando ReservaCreadaEvent: {}", e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadSlotsDTO> getSlots(Long proveedorId, Long servicioId,
                                                 LocalDate desde, LocalDate hasta, Long excluirReservaId) {
        LocalDate from = desde != null ? desde : LocalDate.now();
        LocalDate to = hasta != null ? hasta : from.plusDays(13);
        if (to.isBefore(from)) {
            to = from;
        }
        ServicioInfoDTO servicio = catalogMapper.selectServicio(servicioId);
        int duracion = servicio != null && servicio.getDuracionMinutos() != null
                ? servicio.getDuracionMinutos() : 60;
        List<ModalidadInfoDTO> modalidades = catalogMapper.selectModalidadesByServicio(servicioId);
        List<Disponibilidad> windows = disponibilidadMapper.selectByProveedorServicio(proveedorId, servicioId);
        List<Reserva> booked = reservaMapper.selectBooked(proveedorId, servicioId, from, to, excluirReservaId);
        boolean requiereCertificado = Boolean.TRUE.equals(vacunaCatalogMapper.selectRequiereCertificado(proveedorId, servicioId));

        List<DisponibilidadSlotsDTO> result = new ArrayList<>();
        for (LocalDate fecha = from; !fecha.isAfter(to); fecha = fecha.plusDays(1)) {
            int diaSemana = fecha.getDayOfWeek().getValue() % 7;
            List<SlotDTO> slots = new ArrayList<>();
            for (Disponibilidad window : windows) {
                if (!Integer.valueOf(diaSemana).equals(window.getDiaSemana())) {
                    continue;
                }
                LocalTime t = window.getHoraInicio();
                while (!t.isAfter(window.getHoraFin())) {
                    LocalTime fin = t.plusMinutes(duracion);
                    if (fin.isAfter(window.getHoraFin())) {
                        break;
                    }
                    if (!overlapsBooked(booked, fecha, t, fin)) {
                        slots.add(SlotDTO.builder()
                                .horaInicio(t.toString())
                                .horaFin(fin.toString())
                                .build());
                    }
                    t = fin;
                }
            }
            if (!slots.isEmpty()) {
                result.add(DisponibilidadSlotsDTO.builder()
                        .fecha(fecha)
                        .slots(slots)
                        .requiereCertificado(requiereCertificado)
                        .modalidades(modalidades)
                        .build());
            }
        }
        return result;
    }

    private void validarSlotProveedor(ReservaRequestDTO dto) {
        if (dto.getProveedorId() == null || dto.getFechaInicio() == null || dto.getHoraInicio() == null) {
            return;
        }
        LocalTime fin = dto.getHoraFin() != null ? dto.getHoraFin() : dto.getHoraInicio().plusMinutes(60);
        List<Reserva> booked = reservaMapper.selectBooked(dto.getProveedorId(), dto.getServicioId(),
                dto.getFechaInicio(), dto.getFechaInicio(), null);
        boolean tomado = booked.stream().anyMatch(r ->
                r.getFechaInicio().equals(dto.getFechaInicio())
                        && overlapsBooked(List.of(r), dto.getFechaInicio(), dto.getHoraInicio(), fin));
        if (tomado) {
            throw new BusinessException("El proveedor ya tiene una reserva en ese horario");
        }
        if (!slotCubiertoPorDisponibilidad(dto.getProveedorId(), dto.getServicioId(),
                dto.getFechaInicio(), dto.getHoraInicio(), fin)) {
            throw new BusinessException("El horario seleccionado no está dentro de la disponibilidad del proveedor");
        }
    }

    private boolean slotCubiertoPorDisponibilidad(Long proveedorId, Long servicioId,
                                                  LocalDate fecha, LocalTime inicio, LocalTime fin) {
        List<Disponibilidad> windows = disponibilidadMapper.selectByProveedorServicio(proveedorId, servicioId);
        if (windows.isEmpty()) {
            return true;
        }
        int diaSemana = fecha.getDayOfWeek().getValue() % 7;
        return windows.stream()
                .filter(w -> Integer.valueOf(diaSemana).equals(w.getDiaSemana()))
                .anyMatch(w -> !inicio.isBefore(w.getHoraInicio()) && !fin.isAfter(w.getHoraFin()));
    }

    private String validarModalidad(ReservaRequestDTO dto) {
        String modalidad = dto.getModalidadEntrega();
        if (modalidad == null || modalidad.isBlank()) {
            modalidad = "EN_ESTABLECIMIENTO";
        }
        if (dto.getServicioId() != null
                && !catalogMapper.selectModalidadValida(dto.getServicioId(), modalidad)) {
            throw new BusinessException("La modalidad " + modalidad + " no está disponible para el servicio seleccionado");
        }
        if (!"EN_ESTABLECIMIENTO".equals(modalidad)
                && (dto.getLatitud() == null || dto.getLongitud() == null)) {
            throw new BusinessException("Para la modalidad " + modalidad
                    + " debe indicar la latitud y longitud del lugar");
        }
        return modalidad;
    }

    private void validarCertificado(ReservaRequestDTO dto) {
        if (dto.getProveedorId() == null) {
            return;
        }
        boolean requiere = Boolean.TRUE.equals(
                vacunaCatalogMapper.selectRequiereCertificado(dto.getProveedorId(), dto.getServicioId()));
        if (!requiere && dto.getRegistroVacunacionId() == null) {
            return;
        }
        if (requiere && dto.getRegistroVacunacionId() == null) {
            throw new BusinessException("El servicio requiere adjuntar un certificado de vacunación");
        }
        RegistroVacunacionInfoDTO rv = vacunaCatalogMapper.selectRegistroVacunacion(dto.getRegistroVacunacionId());
        if (rv == null) {
            throw new BusinessException("El registro de vacunación seleccionado no existe");
        }
        if (!rv.getMascotaId().equals(dto.getMascotaId())) {
            throw new BusinessException("El registro de vacunación no pertenece a la mascota seleccionada");
        }
        if (requiere && (rv.getCertificadoUrl() == null || rv.getCertificadoUrl().isBlank())) {
            throw new BusinessException("Debe adjuntar el certificado de vacunación");
        }
    }

    private boolean overlapsBooked(List<Reserva> booked, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        for (Reserva r : booked) {
            if (!r.getFechaInicio().equals(fecha)) {
                continue;
            }
            LocalTime rInicio = r.getHoraInicio();
            LocalTime rFin = r.getHoraFin() != null ? r.getHoraFin() : rInicio.plusMinutes(60);
            if (inicio.isBefore(rFin) && fin.isAfter(rInicio)) {
                return true;
            }
        }
        return false;
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
        validarSlotProveedor(dto);
        validarCertificado(dto);
        String modalidad = validarModalidad(dto);
        reserva.setClienteId(dto.getClienteId());
        reserva.setProveedorId(dto.getProveedorId());
        reserva.setServicioId(dto.getServicioId());
        reserva.setMascotaId(dto.getMascotaId());
        reserva.setRegistroVacunacionId(dto.getRegistroVacunacionId());
        reserva.setModalidadEntrega(modalidad);
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
        String comentario = event.getComentarioProveedor();
        String comentarioHtml = comentario != null && !comentario.isBlank()
                ? "<p><strong>Comentario del proveedor:</strong> " + comentario + "</p>" : "";
        notificarCliente(reserva, "Reserva confirmada",
                "Su reserva " + reserva.getCodigo() + " ha sido aceptada por el proveedor."
                        + (comentario != null && !comentario.isBlank() ? " Comentario: " + comentario : ""), "EXITO");
        enviarEmailCliente(reserva, "Reserva Confirmada - PETCare",
                "<h2>¡Reserva confirmada!</h2>"
                        + "<p>Su reserva <strong>" + reserva.getCodigo() + "</strong> ("
                        + event.getServicioNombre() + ") ha sido aceptada por "
                        + event.getProveedorEmpresa() + ".</p>"
                        + "<p>Fecha: " + reserva.getFechaInicio()
                        + (reserva.getHoraInicio() != null ? " a las " + reserva.getHoraInicio() : "") + "</p>"
                        + comentarioHtml);
        publicarConfirmada(reserva);
        return toResponseDTO(reserva);
    }

    private void publicarConfirmada(Reserva reserva) {
        try {
            eventPublisher.publish(new ReservaConfirmadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), reserva.getProveedorId(),
                    reserva.getServicioId(), reserva.getMascotaId(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal(), reserva.getModalidadEntrega(),
                    reserva.getRegistroVacunacionId()));
        } catch (Exception e) {
            log.warn("Error publicando ReservaConfirmadaEvent: {}", e.getMessage());
        }
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
        notificarCliente(reserva, "Reserva rechazada",
                "Su reserva " + reserva.getCodigo() + " fue rechazada"
                        + (event.getMotivoRechazo() != null && !event.getMotivoRechazo().isBlank()
                        ? ": " + event.getMotivoRechazo() : "") + ".", "ADVERTENCIA");
        enviarEmailCliente(reserva, "Reserva Rechazada - PETCare",
                "<h2>Reserva rechazada</h2>"
                        + "<p>Su reserva <strong>" + reserva.getCodigo() + "</strong> ("
                        + event.getServicioNombre() + ") fue rechazada"
                        + (event.getMotivoRechazo() != null && !event.getMotivoRechazo().isBlank()
                        ? " por el siguiente motivo: " + event.getMotivoRechazo() : "") + ".</p>");
        return toResponseDTO(reserva);
    }

    private void notificarCliente(Reserva reserva, String titulo, String mensaje, String tipo) {
        try {
            if (reserva.getClienteId() == null) {
                return;
            }
            notificacionMapper.insert(Notificacion.builder()
                    .usuarioId(reserva.getClienteId())
                    .titulo(titulo)
                    .mensaje(mensaje)
                    .tipo(tipo)
                    .leida(false)
                    .build());
            log.debug("Notificación creada para cliente {}: {}", reserva.getClienteId(), titulo);
        } catch (Exception e) {
            log.warn("Error creando notificación para reserva {}: {}", reserva.getCodigo(), e.getMessage());
        }
    }

    private void enviarEmailCliente(Reserva reserva, String asunto, String cuerpo) {
        try {
            ClienteInfoDTO cliente = reserva.getClienteId() != null
                    ? catalogMapper.selectCliente(reserva.getClienteId()) : null;
            if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
                log.debug("Cliente sin email, se omite envío para reserva {}", reserva.getCodigo());
                return;
            }
            emailService.sendHtml(cliente.getEmail(), asunto, cuerpo);
        } catch (Exception e) {
            log.warn("Error preparando email para reserva {}: {}", reserva.getCodigo(), e.getMessage());
        }
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
                .registroVacunacionId(reserva.getRegistroVacunacionId())
                .modalidadEntrega(reserva.getModalidadEntrega())
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
                .registroVacunacionId(reserva.getRegistroVacunacionId())
                .modalidadEntrega(reserva.getModalidadEntrega())
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
