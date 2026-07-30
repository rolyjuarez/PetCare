package bo.capital.tec.pet.modules.reserva.service.impl;

import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.entity.Cliente;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.api.MascotaApi;
import bo.capital.tec.pet.modules.mascota.entity.Mascota;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.entity.Persona;
import bo.capital.tec.pet.modules.proveedor.api.ProveedorApi;
import bo.capital.tec.pet.modules.proveedor.entity.Proveedor;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.entity.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
import bo.capital.tec.pet.modules.reserva.event.ReservaCanceladaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCompletadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.reserva.event.ReservaCreadaEvent;
import bo.capital.tec.pet.modules.reserva.mapper.EstadoReservaMapper;
import bo.capital.tec.pet.modules.reserva.mapper.ReservaMapper;
import bo.capital.tec.pet.modules.reserva.service.ReservaService;
import bo.capital.tec.pet.modules.servicio.api.ServicioApi;
import bo.capital.tec.pet.modules.servicio.entity.Servicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaMapper reservaMapper;
    private final EstadoReservaMapper estadoReservaMapper;
    private final ClienteApi clienteApi;
    private final ProveedorApi proveedorApi;
    private final ServicioApi servicioApi;
    private final MascotaApi mascotaApi;
    private final PersonaApi personaApi;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO dto) {
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        EstadoReserva estadoPendiente = estadoReservaMapper.selectByNombre("PENDIENTE");
        Long estadoId = estadoPendiente != null ? estadoPendiente.getId() : 1L;

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
        publishReservaCreada(reserva, response);
        return response;
    }

    private void publishReservaCreada(Reserva reserva, ReservaResponseDTO response) {
        try {
            String clienteEmail = "";
            if (reserva.getClienteId() != null) {
                Cliente cliente = clienteApi.selectById(reserva.getClienteId());
                if (cliente != null && cliente.getPersonaId() != null) {
                    Persona persona = personaApi.selectById(cliente.getPersonaId());
                    if (persona != null) {
                        clienteEmail = persona.getEmail() != null ? persona.getEmail() : "";
                    }
                }
            }
            eventPublisher.publish(new ReservaCreadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(), clienteEmail,
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    reserva.getMascotaId(), response.getMascotaNombre(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal()
            ));
        } catch (Exception e) {
            log.warn("Error publishing ReservaCreadaEvent: {}", e.getMessage());
        }
    }

    private String getClienteEmail(Long clienteId) {
        if (clienteId == null) return "";
        try {
            Cliente cliente = clienteApi.selectById(clienteId);
            if (cliente != null && cliente.getPersonaId() != null) {
                Persona persona = personaApi.selectById(cliente.getPersonaId());
                if (persona != null && persona.getEmail() != null) {
                    return persona.getEmail();
                }
            }
        } catch (Exception e) {
            log.warn("Error getting cliente email: {}", e.getMessage());
        }
        return "";
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO getById(Long id) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReservaSummaryDTO> getAll(Long clienteId, Long mascotaId, Long servicioId,
            String estado, LocalDate fechaDesde, LocalDate fechaHasta, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Reserva> reservas = reservaMapper.selectAll(offset, size);
        long total = reservaMapper.countAll();
        List<ReservaSummaryDTO> content = reservas.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
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
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
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
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        reservaMapper.softDelete(id);
    }

    @Override
    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaMapper.selectByNombre("CONFIRMADA");
        if (estado != null) {
            reservaMapper.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        ReservaResponseDTO response = toResponseDTO(reserva);
        try {
            eventPublisher.publish(new ReservaConfirmadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(), getClienteEmail(reserva.getClienteId()),
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    reserva.getFechaInicio(), reserva.getHoraInicio(),
                    reserva.getPrecioTotal()
            ));
        } catch (Exception e) {
            log.warn("Error publishing ReservaConfirmadaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO rechazar(Long id, String motivo) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaMapper.selectByNombre("RECHAZADA");
        if (estado != null) {
            reservaMapper.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO iniciar(Long id) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaMapper.selectByNombre("EN_PROGRESO");
        if (estado != null) {
            reservaMapper.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO completar(Long id) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaMapper.selectByNombre("COMPLETADA");
        if (estado != null) {
            reservaMapper.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        ReservaResponseDTO response = toResponseDTO(reserva);
        try {
            eventPublisher.publish(new ReservaCompletadaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(), getClienteEmail(reserva.getClienteId()),
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    reserva.getPrecioTotal()
            ));
        } catch (Exception e) {
            log.warn("Error publishing ReservaCompletadaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO cancelar(Long id, String motivo) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaMapper.selectByNombre("CANCELADA");
        if (estado != null) {
            reservaMapper.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        ReservaResponseDTO response = toResponseDTO(reserva);
        try {
            eventPublisher.publish(new ReservaCanceladaEvent(
                    reserva.getId(), reserva.getCodigo(),
                    reserva.getClienteId(), response.getClienteNombre(), getClienteEmail(reserva.getClienteId()),
                    reserva.getProveedorId(), response.getProveedorEmpresa(),
                    reserva.getServicioId(), response.getServicioNombre(),
                    motivo
            ));
        } catch (Exception e) {
            log.warn("Error publishing ReservaCanceladaEvent: {}", e.getMessage());
        }
        return response;
    }

    @Override
    @Transactional
    public ReservaResponseDTO asignarProveedor(Long id, Long proveedorId) {
        Reserva reserva = reservaMapper.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        Proveedor proveedor = proveedorApi.selectById(proveedorId);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", proveedorId);
        }
        reservaMapper.updateProveedor(id, proveedorId);
        reserva.setProveedorId(proveedorId);
        return toResponseDTO(reserva);
    }

    private ReservaResponseDTO toResponseDTO(Reserva reserva) {
        String clienteNombre = "";
        if (reserva.getClienteId() != null) {
            Cliente cliente = clienteApi.selectById(reserva.getClienteId());
            if (cliente != null && cliente.getPersonaId() != null) {
                Persona persona = personaApi.selectById(cliente.getPersonaId());
                if (persona != null) {
                    clienteNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
            }
        }
        String proveedorNombre = "";
        String proveedorEmpresa = "";
        if (reserva.getProveedorId() != null) {
            Proveedor proveedor = proveedorApi.selectById(reserva.getProveedorId());
            if (proveedor != null && proveedor.getPersonaId() != null) {
                Persona persona = personaApi.selectById(proveedor.getPersonaId());
                if (persona != null) {
                    proveedorNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
                proveedorEmpresa = proveedor.getEmpresa() != null ? proveedor.getEmpresa() : "";
            }
        }
        String servicioNombre = "";
        if (reserva.getServicioId() != null) {
            Servicio servicio = servicioApi.selectById(reserva.getServicioId());
            if (servicio != null) {
                servicioNombre = servicio.getNombre();
            }
        }
        String mascotaNombre = "";
        if (reserva.getMascotaId() != null) {
            Mascota mascota = mascotaApi.selectById(reserva.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String estadoNombre = "";
        String estadoColor = "";
        if (reserva.getEstadoReservaId() != null) {
            EstadoReserva estado = estadoReservaMapper.selectById(reserva.getEstadoReservaId());
            if (estado != null) {
                estadoNombre = estado.getNombre();
                estadoColor = estado.getColor();
            }
        }
        return ReservaResponseDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(clienteNombre.trim())
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedorNombre.trim())
                .proveedorEmpresa(proveedorEmpresa)
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicioNombre)
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascotaNombre)
                .estadoReservaId(reserva.getEstadoReservaId())
                .estadoReservaNombre(estadoNombre)
                .estadoReservaColor(estadoColor)
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
                .createdAt(reserva.getCreatedAt())
                .build();
    }

    private ReservaSummaryDTO toSummaryDTO(Reserva reserva) {
        String clienteNombre = "";
        if (reserva.getClienteId() != null) {
            Cliente cliente = clienteApi.selectById(reserva.getClienteId());
            if (cliente != null && cliente.getPersonaId() != null) {
                Persona persona = personaApi.selectById(cliente.getPersonaId());
                if (persona != null) {
                    clienteNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
            }
        }
        String proveedorNombre = "";
        String proveedorEmpresa = "";
        if (reserva.getProveedorId() != null) {
            Proveedor proveedor = proveedorApi.selectById(reserva.getProveedorId());
            if (proveedor != null && proveedor.getPersonaId() != null) {
                Persona persona = personaApi.selectById(proveedor.getPersonaId());
                if (persona != null) {
                    proveedorNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " " +
                            (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                }
                proveedorEmpresa = proveedor.getEmpresa() != null ? proveedor.getEmpresa() : "";
            }
        }
        String servicioNombre = "";
        if (reserva.getServicioId() != null) {
            Servicio servicio = servicioApi.selectById(reserva.getServicioId());
            if (servicio != null) {
                servicioNombre = servicio.getNombre();
            }
        }
        String mascotaNombre = "";
        if (reserva.getMascotaId() != null) {
            Mascota mascota = mascotaApi.selectById(reserva.getMascotaId());
            if (mascota != null) {
                mascotaNombre = mascota.getNombre();
            }
        }
        String estadoNombre = "";
        String estadoColor = "";
        if (reserva.getEstadoReservaId() != null) {
            EstadoReserva estado = estadoReservaMapper.selectById(reserva.getEstadoReservaId());
            if (estado != null) {
                estadoNombre = estado.getNombre();
                estadoColor = estado.getColor();
            }
        }
        return ReservaSummaryDTO.builder()
                .id(reserva.getId())
                .codigo(reserva.getCodigo())
                .clienteId(reserva.getClienteId())
                .clienteNombre(clienteNombre.trim())
                .proveedorId(reserva.getProveedorId())
                .proveedorNombre(proveedorNombre.trim())
                .proveedorEmpresa(proveedorEmpresa)
                .servicioId(reserva.getServicioId())
                .servicioNombre(servicioNombre)
                .mascotaId(reserva.getMascotaId())
                .mascotaNombre(mascotaNombre)
                .estadoReservaId(reserva.getEstadoReservaId())
                .estadoReservaNombre(estadoNombre)
                .estadoReservaColor(estadoColor)
                .fechaReserva(reserva.getFechaReserva())
                .fechaInicio(reserva.getFechaInicio())
                .fechaFin(reserva.getFechaFin())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .precioTotal(reserva.getPrecioTotal())
                .build();
    }
}
