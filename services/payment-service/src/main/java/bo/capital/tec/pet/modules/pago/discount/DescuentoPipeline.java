package bo.capital.tec.pet.modules.pago.discount;

import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import bo.capital.tec.pet.modules.pago.mapper.PagoCatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Construye y ejecuta la cadena de eslabones (patrón PIPE) que ajusta el
 * monto de un pago: descuento por promoción -> recargo por modalidad.
 */
@Component
@RequiredArgsConstructor
public class DescuentoPipeline {

    private final PagoCatalogMapper catalogMapper;

    public PagoContext ejecutar(ReservaInfoDTO reserva) {
        PagoContext context = PagoContext.of(reserva);

        DescuentoPipe promocion = new PipePromocionProveedor(catalogMapper);
        DescuentoPipe recargo = new PipeRecargoModalidad(catalogMapper);
        DescuentoPipe terminal = new PipeTerminal();

        promocion.setNext(recargo);
        recargo.setNext(terminal);

        promocion.procesar(context);
        return context;
    }
}
