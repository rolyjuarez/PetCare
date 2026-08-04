package bo.capital.tec.pet.modules.pago.discount;

import bo.capital.tec.pet.modules.pago.dto.DescuentoAplicadoDTO;
import bo.capital.tec.pet.modules.pago.dto.ReservaInfoDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PagoContext {

    private ReservaInfoDTO reserva;
    private BigDecimal montoOriginal;
    private BigDecimal montoActual;
    private BigDecimal descuentoTotal = BigDecimal.ZERO;
    private final List<DescuentoAplicadoDTO> descuentosAplicados = new ArrayList<>();

    public static PagoContext of(ReservaInfoDTO reserva) {
        PagoContext ctx = new PagoContext();
        ctx.setReserva(reserva);
        ctx.setMontoOriginal(reserva.getPrecioTotal());
        ctx.setMontoActual(reserva.getPrecioTotal());
        return ctx;
    }

    public void aplicarDescuento(DescuentoAplicadoDTO descuento) {
        descuentosAplicados.add(descuento);
        descuentoTotal = descuentoTotal.add(descuento.getMonto());
        montoActual = montoActual.subtract(descuento.getMonto());
    }

    public void aplicarRecargo(BigDecimal monto) {
        montoActual = montoActual.add(monto);
    }
}
