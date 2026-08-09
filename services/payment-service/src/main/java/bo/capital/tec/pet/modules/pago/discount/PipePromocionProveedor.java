package bo.capital.tec.pet.modules.pago.discount;

import bo.capital.tec.pet.modules.pago.dto.DescuentoAplicadoDTO;
import bo.capital.tec.pet.modules.pago.dto.PromocionInfoDTO;
import bo.capital.tec.pet.modules.pago.mapper.PagoCatalogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Eslabón que aplica la promoción activa del proveedor (PERCENTAGE o FIXED).
 */
@Slf4j
@RequiredArgsConstructor
public class PipePromocionProveedor extends AbstractDescuentoPipe {

    public static final String TIPO_PERCENTAGE = "PERCENTAGE";
    public static final String TIPO_FIXED = "FIXED";

    private final PagoCatalogMapper catalogMapper;

    @Override
    public void procesar(PagoContext context) {
        Long proveedorId = context.getReserva() != null ? context.getReserva().getProveedorId() : null;
        Long servicioId = context.getReserva() != null ? context.getReserva().getServicioId() : null;
        PromocionInfoDTO promocion = proveedorId != null
                ? catalogMapper.selectPromocionActivaByProveedorYServicio(proveedorId, servicioId) : null;
        if (promocion != null && promocion.getValor() != null
                && promocion.getValor().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuento = TIPO_PERCENTAGE.equals(promocion.getTipo())
                    ? context.getMontoActual()
                        .multiply(promocion.getValor())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    : promocion.getValor();
            if (descuento.compareTo(context.getMontoActual()) > 0) {
                descuento = context.getMontoActual();
            }
            context.aplicarDescuento(DescuentoAplicadoDTO.builder()
                    .id(promocion.getId())
                    .codigo(promocion.getCodigo())
                    .nombre(promocion.getNombre())
                    .tipo(promocion.getTipo())
                    .monto(descuento)
                    .servicioId(promocion.getServicioId() != null
                            ? promocion.getServicioId() : servicioId)
                    .build());
            catalogMapper.incrementarUsosPromocion(promocion.getId());
            log.debug("Promoción '{}' aplicada: -{}", promocion.getNombre(), descuento);
        }
        delegar(context);
    }
}
