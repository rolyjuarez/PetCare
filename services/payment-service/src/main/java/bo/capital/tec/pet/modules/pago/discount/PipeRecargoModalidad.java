package bo.capital.tec.pet.modules.pago.discount;

import bo.capital.tec.pet.common.client.PagoProviderClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Eslabón que suma el costo adicional de la modalidad de entrega
 * (RECOGIDA_ENTREGA o DOMICILIO) cuando aplica.
 */
@Slf4j
@RequiredArgsConstructor
public class PipeRecargoModalidad extends AbstractDescuentoPipe {

    private final PagoProviderClient providerClient;

    @Override
    public void procesar(PagoContext context) {
        Long servicioId = context.getReserva() != null ? context.getReserva().getServicioId() : null;
        String modalidad = context.getReserva() != null ? context.getReserva().getModalidadEntrega() : null;
        if (servicioId != null && modalidad != null
                && !"EN_ESTABLECIMIENTO".equals(modalidad)) {
            BigDecimal recargo = providerClient.getCostoAdicionalModalidad(servicioId, modalidad);
            if (recargo != null && recargo.compareTo(BigDecimal.ZERO) > 0) {
                context.aplicarRecargo(recargo);
                log.debug("Recargo por modalidad {}: +{}", modalidad, recargo);
            }
        }
        delegar(context);
    }
}
