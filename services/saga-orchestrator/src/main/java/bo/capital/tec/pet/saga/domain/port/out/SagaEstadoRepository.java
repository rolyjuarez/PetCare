package bo.capital.tec.pet.saga.domain.port.out;

import bo.capital.tec.pet.saga.domain.model.EstadoSaga;
import bo.capital.tec.pet.saga.domain.model.SagaEstado;

import java.util.Optional;

/**
 * Puerto de salida: persistencia de la proyección de estado de la saga.
 */
public interface SagaEstadoRepository {

    Optional<SagaEstado> findByReservaId(Long reservaId);

    void insert(SagaEstado estado);

    void updateEstado(Long reservaId, EstadoSaga estado, String pasoActual,
                      String motivo, String eventoId);
}
