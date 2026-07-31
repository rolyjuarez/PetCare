package bo.capital.tec.pet.modules.reserva.application.service.impl;

import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.domain.model.Cliente;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.mascota.api.MascotaApi;
import bo.capital.tec.pet.modules.mascota.domain.model.Mascota;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.domain.model.Persona;
import bo.capital.tec.pet.modules.proveedor.api.ProveedorApi;
import bo.capital.tec.pet.modules.proveedor.domain.model.Proveedor;
import bo.capital.tec.pet.modules.reserva.dto.ReservaRequestDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaResponseDTO;
import bo.capital.tec.pet.modules.reserva.dto.ReservaSummaryDTO;
import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.EstadoReservaRepository;
import bo.capital.tec.pet.modules.reserva.domain.port.ReservaRepository;
import bo.capital.tec.pet.modules.reserva.application.service.ReservaService;
import bo.capital.tec.pet.modules.servicio.api.ServicioApi;
import bo.capital.tec.pet.modules.servicio.domain.model.Servicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final EstadoReservaRepository estadoReservaRepository;
    private final ClienteApi clienteApi;
    private final ProveedorApi proveedorApi;
    private final ServicioApi servicioApi;
    private final MascotaApi mascotaApi;
    private final PersonaApi personaApi;

    @Override
    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO dto) {
        String codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        EstadoReserva estadoPendiente = estadoReservaRepository.selectByNombre("PENDIENTE");
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
        reservaRepository.insert(reserva);
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO getById(Long id) {
        Reserva reserva = reservaRepository.selectById(id);
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
        List<Reserva> reservas = reservaRepository.selectAll(offset, size);
        long total = reservaRepository.countAll();
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
        Reserva reserva = reservaRepository.selectById(id);
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
        reservaRepository.update(reserva);
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        reservaRepository.softDelete(id);
    }

    @Override
    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaRepository.selectByNombre("CONFIRMADA");
        if (estado != null) {
            reservaRepository.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO rechazar(Long id, String motivo) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaRepository.selectByNombre("RECHAZADA");
        if (estado != null) {
            reservaRepository.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO iniciar(Long id) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaRepository.selectByNombre("EN_PROGRESO");
        if (estado != null) {
            reservaRepository.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO completar(Long id) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaRepository.selectByNombre("COMPLETADA");
        if (estado != null) {
            reservaRepository.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO cancelar(Long id, String motivo) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        EstadoReserva estado = estadoReservaRepository.selectByNombre("CANCELADA");
        if (estado != null) {
            reservaRepository.updateEstado(id, estado.getId());
            reserva.setEstadoReservaId(estado.getId());
        }
        return toResponseDTO(reserva);
    }

    @Override
    @Transactional
    public ReservaResponseDTO asignarProveedor(Long id, Long proveedorId) {
        Reserva reserva = reservaRepository.selectById(id);
        if (reserva == null) {
            throw new EntityNotFoundException("Reserva", id);
        }
        Proveedor proveedor = proveedorApi.selectById(proveedorId);
        if (proveedor == null) {
            throw new EntityNotFoundException("Proveedor", proveedorId);
        }
        reservaRepository.updateProveedor(id, proveedorId);
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
            EstadoReserva estado = estadoReservaRepository.selectById(reserva.getEstadoReservaId());
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
            EstadoReserva estado = estadoReservaRepository.selectById(reserva.getEstadoReservaId());
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
