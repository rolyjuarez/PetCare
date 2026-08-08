package bo.capital.tec.pet.saga.domain.port.out;

import bo.capital.tec.pet.saga.domain.model.SagaCommand;

/**
 * Puerto de salida: publicación de comandos hacia los microservicios.
 */
public interface SagaCommandPublisher {

    void publicar(SagaCommand comando);
}
