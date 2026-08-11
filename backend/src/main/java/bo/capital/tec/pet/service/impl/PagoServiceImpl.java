package bo.capital.tec.pet.service.impl;

import bo.capital.tec.pet.common.exceptions.EntityNotFoundException;
import bo.capital.tec.pet.common.response.PagedResponse;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.dto.PagoRequestDTO;
import bo.capital.tec.pet.dto.PagoResponseDTO;
import bo.capital.tec.pet.dto.PagoSummaryDTO;
import bo.capital.tec.pet.domain.Pago;
import bo.capital.tec.pet.repository.PagoMapper;
import bo.capital.tec.pet.service.PagoService;
import bo.capital.tec.pet.domain.Reserva;
import bo.capital.tec.pet.repository.ReservaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private final PagoMapper pagoMapper;
    private final ReservaMapper reservaMapper;

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
        return toResponseDTO(pago);
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
        return toResponseDTO(pago);
    }

    private PagoResponseDTO toResponseDTO(Pago pago) {
        String reservaCodigo = "";
        if (pago.getReservaId() != null) {
            Reserva reserva = reservaMapper.selectById(pago.getReservaId());
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
            Reserva reserva = reservaMapper.selectById(pago.getReservaId());
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
