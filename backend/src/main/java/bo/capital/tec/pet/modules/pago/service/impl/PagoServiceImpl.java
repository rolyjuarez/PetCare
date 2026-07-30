package bo.capital.tec.pet.modules.pago.service.impl;

import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.cliente.api.ClienteApi;
import bo.capital.tec.pet.modules.cliente.entity.Cliente;
import bo.capital.tec.pet.modules.pago.dto.PagoRequestDTO;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.PagoSummaryDTO;
import bo.capital.tec.pet.modules.pago.entity.Pago;
import bo.capital.tec.pet.modules.pago.event.PagoConfirmadoEvent;
import bo.capital.tec.pet.modules.pago.event.PagoReembolsadoEvent;
import bo.capital.tec.pet.modules.pago.mapper.PagoMapper;
import bo.capital.tec.pet.modules.pago.service.PagoService;
import bo.capital.tec.pet.modules.persona.api.PersonaApi;
import bo.capital.tec.pet.modules.persona.entity.Persona;
import bo.capital.tec.pet.modules.reserva.api.ReservaApi;
import bo.capital.tec.pet.modules.reserva.entity.Reserva;
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
public class PagoServiceImpl implements PagoService {

    private final PagoMapper pagoMapper;
    private final ReservaApi reservaApi;
    private final ClienteApi clienteApi;
    private final PersonaApi personaApi;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public PagoResponseDTO create(PagoRequestDTO dto) {
        Pago existing = pagoMapper.selectByReservaId(dto.getReservaId());
        if (existing != null) {
            throw new bo.capital.tec.pet.common.exceptions.BusinessException("Ya existe un pago para la reserva con id: " + dto.getReservaId());
        }
        Pago pago = Pago.builder()
                .reservaId(dto.getReservaId())
                .monto(dto.getMonto())
                .metodoPago(dto.getMetodoPago())
                .estadoPago("PENDIENTE")
                .referenciaTransaccion(dto.getReferenciaTransaccion())
                .fechaPago(LocalDateTime.now())
                .comprobanteUrl(dto.getComprobanteUrl())
                .build();
        pagoMapper.insert(pago);
        return toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO getById(Long id) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        return toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PagoSummaryDTO> getAll(Long reservaId, Long clienteId, String metodoPago,
            String estado, LocalDate fechaDesde, LocalDate fechaHasta, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        int offset = page * size;
        List<Pago> pagos = pagoMapper.selectAll(offset, size);
        long total = pagoMapper.countAll();
        List<PagoSummaryDTO> content = pagos.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
        return PagedResponse.<PagoSummaryDTO>builder()
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
    public PagoResponseDTO update(Long id, PagoRequestDTO dto) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        pago.setReservaId(dto.getReservaId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setReferenciaTransaccion(dto.getReferenciaTransaccion());
        pago.setComprobanteUrl(dto.getComprobanteUrl());
        pagoMapper.update(pago);
        return toResponseDTO(pago);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        pagoMapper.softDelete(id);
    }

    @Override
    @Transactional
    public PagoResponseDTO confirmarPago(Long id) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        pagoMapper.updateEstadoPago(id, "CONFIRMADO");
        pago.setEstadoPago("CONFIRMADO");
        PagoResponseDTO response = toResponseDTO(pago);
        publishPagoConfirmado(pago, response);
        return response;
    }

    private void publishPagoConfirmado(Pago pago, PagoResponseDTO response) {
        try {
            Reserva reserva = reservaApi.selectById(pago.getReservaId());
            if (reserva != null) {
                String clienteNombre = "";
                String clienteEmail = "";
                if (reserva.getClienteId() != null) {
                    Cliente cliente = clienteApi.selectById(reserva.getClienteId());
                    if (cliente != null && cliente.getPersonaId() != null) {
                        Persona persona = personaApi.selectById(cliente.getPersonaId());
                        if (persona != null) {
                            clienteNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " "
                                    + (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                            clienteNombre = clienteNombre.trim();
                            clienteEmail = persona.getEmail() != null ? persona.getEmail() : "";
                        }
                    }
                }
                eventPublisher.publish(new PagoConfirmadoEvent(
                        pago.getId(), pago.getReservaId(), reserva.getCodigo(),
                        pago.getMonto(), pago.getMetodoPago(), pago.getReferenciaTransaccion(),
                        reserva.getClienteId(), clienteNombre, clienteEmail
                ));
            }
        } catch (Exception e) {
            log.warn("Error publishing PagoConfirmadoEvent: {}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public PagoResponseDTO reembolsar(Long id, String motivo) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        pagoMapper.updateEstadoPago(id, "REEMBOLSADO");
        pago.setEstadoPago("REEMBOLSADO");
        PagoResponseDTO response = toResponseDTO(pago);
        publishPagoReembolsado(pago, response, motivo);
        return response;
    }

    private void publishPagoReembolsado(Pago pago, PagoResponseDTO response, String motivo) {
        try {
            Reserva reserva = reservaApi.selectById(pago.getReservaId());
            if (reserva != null) {
                String clienteNombre = "";
                String clienteEmail = "";
                if (reserva.getClienteId() != null) {
                    Cliente cliente = clienteApi.selectById(reserva.getClienteId());
                    if (cliente != null && cliente.getPersonaId() != null) {
                        Persona persona = personaApi.selectById(cliente.getPersonaId());
                        if (persona != null) {
                            clienteNombre = (persona.getNombre() != null ? persona.getNombre() : "") + " "
                                    + (persona.getPrimerApellido() != null ? persona.getPrimerApellido() : "");
                            clienteNombre = clienteNombre.trim();
                            clienteEmail = persona.getEmail() != null ? persona.getEmail() : "";
                        }
                    }
                }
                eventPublisher.publish(new PagoReembolsadoEvent(
                        pago.getId(), pago.getReservaId(), reserva.getCodigo(),
                        pago.getMonto(), reserva.getClienteId(), clienteNombre, clienteEmail, motivo
                ));
            }
        } catch (Exception e) {
            log.warn("Error publishing PagoReembolsadoEvent: {}", e.getMessage());
        }
    }

    private PagoResponseDTO toResponseDTO(Pago pago) {
        String reservaCodigo = "";
        if (pago.getReservaId() != null) {
            Reserva reserva = reservaApi.selectById(pago.getReservaId());
            if (reserva != null) {
                reservaCodigo = reserva.getCodigo();
            }
        }
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .reservaId(pago.getReservaId())
                .reservaCodigo(reservaCodigo)
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .referenciaTransaccion(pago.getReferenciaTransaccion())
                .fechaPago(pago.getFechaPago())
                .comprobanteUrl(pago.getComprobanteUrl())
                .createdAt(pago.getCreatedAt())
                .build();
    }

    private PagoSummaryDTO toSummaryDTO(Pago pago) {
        String reservaCodigo = "";
        if (pago.getReservaId() != null) {
            Reserva reserva = reservaApi.selectById(pago.getReservaId());
            if (reserva != null) {
                reservaCodigo = reserva.getCodigo();
            }
        }
        return PagoSummaryDTO.builder()
                .id(pago.getId())
                .reservaCodigo(reservaCodigo)
                .monto(pago.getMonto())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .fechaPago(pago.getFechaPago())
                .build();
    }
}
