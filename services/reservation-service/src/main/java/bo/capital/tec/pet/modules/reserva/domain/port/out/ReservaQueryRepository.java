package bo.capital.tec.pet.modules.reserva.domain.port.out;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;

import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de salida del lado lectura (CQRS read side).
 *
 * <p>Operaciones de consulta optimizadas que alimentan a
 * {@code ReservaQueryService}; no modifican el estado del sistema.</p>
 */
public interface ReservaQueryRepository {

    Reserva findById(Long id);

    List<Reserva> findAll(int offset, int limit);

    long countAll();

    List<Reserva> findByClienteId(Long clienteId, int offset, int limit);

    long countByClienteId(Long clienteId);

    List<Reserva> findByProveedorId(Long proveedorId, int offset, int limit);

    long countByProveedorId(Long proveedorId);

    List<Reserva> findFiltered(Long clienteId, Long mascotaId, Long servicioId,
                               String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                               int offset, int limit);

    long countFiltered(Long clienteId, Long mascotaId, Long servicioId,
                       String estado, LocalDate fechaDesde, LocalDate fechaHasta);

    List<Reserva> findBooked(Long proveedorId, Long servicioId,
                             LocalDate desde, LocalDate hasta, Long excluirId);
}
