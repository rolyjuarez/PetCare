package bo.capital.tec.pet.modules.pago.service.impl;

import bo.capital.tec.pet.common.api.PagedResponse;
import bo.capital.tec.pet.common.event.DomainEventPublisher;
import bo.capital.tec.pet.common.exception.BusinessException;
import bo.capital.tec.pet.common.exception.EntityNotFoundException;
import bo.capital.tec.pet.common.util.PaginationUtil;
import bo.capital.tec.pet.modules.pago.discount.DescuentoPipeline;
import bo.capital.tec.pet.modules.pago.discount.PagoContext;
import bo.capital.tec.pet.modules.pago.dto.PagoResponseDTO;
import bo.capital.tec.pet.modules.pago.dto.ProcesarPagoRequestDTO;
import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import bo.capital.tec.pet.modules.pago.entity.Pago;
import bo.capital.tec.pet.modules.pago.event.PagoProcesadoEvent;
import bo.capital.tec.pet.modules.pago.event.ReservaConfirmadaEvent;
import bo.capital.tec.pet.modules.pago.mapper.PagoCatalogMapper;
import bo.capital.tec.pet.modules.pago.mapper.PagoMapper;
import bo.capital.tec.pet.modules.pago.pasarela.IntencionPago;
import bo.capital.tec.pet.modules.pago.pasarela.ResultadoPasarela;
import bo.capital.tec.pet.modules.pago.pasarela.SimulatedPaymentGateway;
import bo.capital.tec.pet.modules.pago.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    private static final String ESTADO_COMPLETADO = "COMPLETADO";
    private static final String ESTADO_FALLIDO = "FALLIDO";
    private static final String ESTADO_REEMBOLSADO = "REEMBOLSADO";

    private static final String MODALIDAD_EN_LINEA = "EN_LINEA";
    private static final String MODALIDAD_EN_ESTABLECIMIENTO = "EN_ESTABLECIMIENTO";

    private final PagoMapper pagoMapper;
    private final PagoCatalogMapper catalogMapper;
    private final DescuentoPipeline descuentoPipeline;
    private final SimulatedPaymentGateway pasarela;
    private final DomainEventPublisher eventPublisher;

    @Override
    @Transactional
    public PagoResponseDTO crearDesdeReservaConfirmada(ReservaConfirmadaEvent event) {
        Pago existente = pagoMapper.selectByReservaId(event.getReservaId());
        if (existente != null) {
            return toResponseDTO(existente);
        }
        ReservaInfoDTO reserva = catalogMapper.selectReservaInfo(event.getReservaId());
        if (reserva == null) {
            throw new BusinessException("La reserva " + event.getReservaId() + " no existe");
        }
        String modalidadPago = MODALIDAD_EN_ESTABLECIMIENTO.equals(reserva.getModalidadEntrega())
                ? MODALIDAD_EN_ESTABLECIMIENTO : MODALIDAD_EN_LINEA;
        Pago pago = Pago.builder()
                .reservaId(event.getReservaId())
                .monto(event.getPrecioTotal())
                .montoOriginal(event.getPrecioTotal())
                .descuentoTotal(BigDecimal.ZERO)
                .metodoPago(MODALIDAD_EN_ESTABLECIMIENTO.equals(modalidadPago) ? "EN_ESTABLECIMIENTO" : "SIN_DEFINIR")
                .estadoPago(ESTADO_PENDIENTE)
                .estadoSync(ESTADO_PENDIENTE)
                .modalidadPago(modalidadPago)
                .build();
        pagoMapper.insert(pago);
        log.info("Pago {} creado para reserva confirmada {}", pago.getId(), event.getCodigo());
        return toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO getById(Long id) {
        return toResponseDTO(requirePago(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO getByReservaId(Long reservaId) {
        Pago pago = pagoMapper.selectByReservaId(reservaId);
        if (pago == null) {
            throw new EntityNotFoundException("Pago por reserva", reservaId);
        }
        return toResponseDTO(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PagoResponseDTO> listByReservaId(Long reservaId, int page, int size) {
        page = PaginationUtil.safePage(page);
        size = PaginationUtil.safeSize(size);
        List<Pago> pagos = pagoMapper.selectByReservaIds(reservaId, size, page * size);
        long total = pagoMapper.countByReservaId(reservaId);
        return PagedResponse.<PagoResponseDTO>builder()
                .content(pagos.stream().map(this::toResponseDTO).toList())
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
    public PagoResponseDTO procesar(Long id, ProcesarPagoRequestDTO request) {
        Pago pago = requirePago(id);
        if (ESTADO_COMPLETADO.equals(pago.getEstadoSync()) || ESTADO_REEMBOLSADO.equals(pago.getEstadoSync())) {
            throw new BusinessException("El pago ya fue " + pago.getEstadoSync().toLowerCase());
        }
        ReservaInfoDTO reserva = catalogMapper.selectReservaInfo(pago.getReservaId());
        PagoContext context = descuentoPipeline.ejecutar(reserva);
        pagoMapper.updateDescuentos(pago.getId(), context.getMontoActual(),
                context.getMontoOriginal(), context.getDescuentoTotal());

        if (MODALIDAD_EN_ESTABLECIMIENTO.equals(pago.getModalidadPago())) {
            pagoMapper.updateEstado(pago.getId(), ESTADO_COMPLETADO, ESTADO_COMPLETADO,
                    "EFECTIVO-EN-ESTABLECIMIENTO", null, LocalDateTime.now(), "EFECTIVO");
            publishProcesado(pago, request);
            return getById(id);
        }

        if (request.getTarjeta() == null) {
            throw new BusinessException("Para pagos en línea debe indicar los datos de la tarjeta");
        }
        pagoMapper.updateEstado(pago.getId(), ESTADO_EN_PROCESO, ESTADO_EN_PROCESO,
                null, null, null, request.getMetodoPago());
        ResultadoPasarela resultado = pasarela.procesar(IntencionPago.builder()
                .pagoId(pago.getId())
                .reservaId(pago.getReservaId())
                .monto(context.getMontoActual())
                .metodoPago(request.getMetodoPago())
                .numeroTarjeta(request.getTarjeta().getNumero())
                .titularTarjeta(request.getTarjeta().getTitular())
                .expiraTarjeta(request.getTarjeta().getExpira())
                .cvvTarjeta(request.getTarjeta().getCvv())
                .build());
        if (resultado.isAprobado()) {
            pagoMapper.updateEstado(pago.getId(), ESTADO_COMPLETADO, ESTADO_COMPLETADO,
                    resultado.getReferenciaTransaccion(), resultado.getIntencionId(),
                    LocalDateTime.now(), request.getMetodoPago());
            log.info("Pago {} completado, referencia {}", pago.getId(), resultado.getReferenciaTransaccion());
        } else {
            pagoMapper.updateEstado(pago.getId(), ESTADO_FALLIDO, ESTADO_FALLIDO,
                    null, resultado.getIntencionId(), null, request.getMetodoPago());
            log.warn("Pago {} fallido: {}", pago.getId(), resultado.getMensaje());
        }
        publishProcesado(pago, request);
        return getById(id);
    }

    @Override
    @Transactional
    public PagoResponseDTO reembolsar(Long id) {
        Pago pago = requirePago(id);
        if (!ESTADO_COMPLETADO.equals(pago.getEstadoSync())) {
            throw new BusinessException("Solo se puede reembolsar un pago completado");
        }
        pagoMapper.updateEstado(pago.getId(), ESTADO_REEMBOLSADO, ESTADO_REEMBOLSADO,
                pago.getReferenciaTransaccion(), pago.getIntencionId(), LocalDateTime.now(),
                pago.getMetodoPago());
        log.info("Pago {} reembolsado", pago.getId());
        return getById(id);
    }

    private void publishProcesado(Pago pago, ProcesarPagoRequestDTO request) {
        try {
            Pago actualizado = pagoMapper.selectById(pago.getId());
            eventPublisher.publish(new PagoProcesadoEvent(
                    actualizado.getId(), actualizado.getReservaId(),
                    actualizado.getMonto(), actualizado.getMontoOriginal(),
                    actualizado.getDescuentoTotal(),
                    actualizado.getMetodoPago() != null ? actualizado.getMetodoPago() : request.getMetodoPago(),
                    actualizado.getEstadoSync(), actualizado.getReferenciaTransaccion()));
        } catch (Exception e) {
            log.warn("Error publicando PagoProcesadoEvent: {}", e.getMessage());
        }
    }

    private Pago requirePago(Long id) {
        Pago pago = pagoMapper.selectById(id);
        if (pago == null) {
            throw new EntityNotFoundException("Pago", id);
        }
        return pago;
    }

    private PagoResponseDTO toResponseDTO(Pago pago) {
        ReservaInfoDTO reserva = null;
        try {
            reserva = catalogMapper.selectReservaInfo(pago.getReservaId());
        } catch (Exception e) {
            log.debug("No se pudo cargar información de reserva para pago {}", pago.getId());
        }
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .reservaId(pago.getReservaId())
                .codigoReserva(reserva != null ? reserva.getCodigo() : "")
                .monto(pago.getMonto())
                .montoOriginal(pago.getMontoOriginal())
                .descuentoTotal(pago.getDescuentoTotal())
                .metodoPago(pago.getMetodoPago())
                .estadoPago(pago.getEstadoPago())
                .referenciaTransaccion(pago.getReferenciaTransaccion())
                .fechaPago(pago.getFechaPago())
                .modalidadPago(pago.getModalidadPago())
                .intencionId(pago.getIntencionId())
                .estadoSync(pago.getEstadoSync())
                .build();
    }
}
