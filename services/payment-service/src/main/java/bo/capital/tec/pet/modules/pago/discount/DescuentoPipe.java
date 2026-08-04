package bo.capital.tec.pet.modules.pago.discount;

/**
 * Patrón PIPE: cada eslabón puede aplicar un ajuste al monto del contexto y
 * delegar al siguiente. La cadena finaliza con un eslabón terminal.
 */
public interface DescuentoPipe {

    void procesar(PagoContext context);

    void setNext(DescuentoPipe next);

    DescuentoPipe getNext();
}
