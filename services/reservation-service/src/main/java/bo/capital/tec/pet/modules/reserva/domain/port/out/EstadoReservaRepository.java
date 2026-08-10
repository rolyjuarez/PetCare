package bo.capital.tec.pet.modules.reserva.domain.port.out;

import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;

/**
 * Puerto de salida para el catálogo de estados de reserva.
 */
public interface EstadoReservaRepository {

    EstadoReserva findById(Long id);

    EstadoReserva findByNombre(String nombre);
}
