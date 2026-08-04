package bo.capital.tec.pet.modules.pago.pasarela;

public interface SimulatedPaymentGateway {

    ResultadoPasarela procesar(IntencionPago intencion);
}
