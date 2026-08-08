package bo.capital.tec.pet.saga.domain.port.in;

import bo.capital.tec.pet.saga.domain.model.PagoFallidoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoProcesadoEvent;
import bo.capital.tec.pet.saga.domain.model.PagoReembolsadoEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaAceptadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCanceladaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaConfirmadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaCreadaEvent;
import bo.capital.tec.pet.saga.domain.model.ReservaRechazadaEvent;

/**
 * Puerto de entrada: casos de uso que el orquestador de la saga debe ejecutar
 * cuando llega cada evento de dominio.
 */
public interface SagaCoordinator {

    void onReservaCreada(ReservaCreadaEvent evento);

    void onReservaAceptada(ReservaAceptadaEvent evento);

    void onReservaRechazada(ReservaRechazadaEvent evento);

    void onReservaConfirmada(ReservaConfirmadaEvent evento);

    void onReservaCancelada(ReservaCanceladaEvent evento);

    void onPagoProcesado(PagoProcesadoEvent evento);

    void onPagoFallido(PagoFallidoEvent evento);

    void onPagoReembolsado(PagoReembolsadoEvent evento);
}
