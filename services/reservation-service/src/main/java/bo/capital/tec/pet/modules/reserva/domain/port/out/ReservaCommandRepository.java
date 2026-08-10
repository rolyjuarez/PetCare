package bo.capital.tec.pet.modules.reserva.domain.port.out;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida del lado escritura (CQRS write side).
 *
 * <p>Operaciones de persistencia que los casos de uso de comando necesitan para
 * guardar y cambiar el estado de una reserva, así como para consultar datos
 * necesarios en las validaciones de negocio.</p>
 */
public interface ReservaCommandRepository {

    Reserva insert(Reserva reserva);

    void update(Reserva reserva);

    void updateEstado(Long id, Long estadoReservaId);

    void updateRespuesta(Long id, Long proveedorId, Long estadoReservaId, String motivoRechazo);

    void softDelete(Long id);

    Reserva findById(Long id);

    List<Reserva> findActivasPorCliente(Long clienteId);

    List<Reserva> findBooked(Long proveedorId, Long servicioId,
                             LocalDate desde, LocalDate hasta, Long excluirId);
}
