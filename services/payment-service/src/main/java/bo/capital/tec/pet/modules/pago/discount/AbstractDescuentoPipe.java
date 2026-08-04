package bo.capital.tec.pet.modules.pago.discount;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public abstract class AbstractDescuentoPipe implements DescuentoPipe {

    protected DescuentoPipe next;

    @Override
    public void setNext(DescuentoPipe next) {
        this.next = next;
    }

    @Override
    public DescuentoPipe getNext() {
        return next;
    }

    protected void delegar(PagoContext context) {
        if (next != null) {
            next.procesar(context);
        }
    }

    protected BigDecimal zero() {
        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    }
}
