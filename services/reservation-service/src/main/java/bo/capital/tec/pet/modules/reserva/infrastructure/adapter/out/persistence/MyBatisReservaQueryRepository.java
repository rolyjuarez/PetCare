package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación MyBatis del puerto de repositorio del lado lectura.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisReservaQueryRepository implements ReservaQueryRepository {

    private final ReservaMapper reservaMapper;

    @Override
    public Reserva findById(Long id) {
        return reservaMapper.selectById(id);
    }

    @Override
    public List<Reserva> findAll(int offset, int limit) {
        return reservaMapper.selectAll(offset, limit);
    }

    @Override
    public long countAll() {
        return reservaMapper.countAll();
    }

    @Override
    public List<Reserva> findByClienteId(Long clienteId, int offset, int limit) {
        return reservaMapper.selectByClienteId(clienteId, offset, limit);
    }

    @Override
    public long countByClienteId(Long clienteId) {
        return reservaMapper.countByClienteId(clienteId);
    }

    @Override
    public List<Reserva> findByProveedorId(Long proveedorId, int offset, int limit) {
        return reservaMapper.selectByProveedorId(proveedorId, offset, limit);
    }

    @Override
    public long countByProveedorId(Long proveedorId) {
        return reservaMapper.countByProveedorId(proveedorId);
    }

    @Override
    public List<Reserva> findFiltered(Long clienteId, Long mascotaId, Long servicioId,
                                      String estado, LocalDate fechaDesde, LocalDate fechaHasta,
                                      int offset, int limit) {
        return reservaMapper.selectFiltered(clienteId, mascotaId, servicioId,
                estado, fechaDesde, fechaHasta, offset, limit);
    }

    @Override
    public long countFiltered(Long clienteId, Long mascotaId, Long servicioId,
                              String estado, LocalDate fechaDesde, LocalDate fechaHasta) {
        return reservaMapper.countFiltered(clienteId, mascotaId, servicioId,
                estado, fechaDesde, fechaHasta);
    }

    @Override
    public List<Reserva> findBooked(Long proveedorId, Long servicioId,
                                    LocalDate desde, LocalDate hasta, Long excluirId) {
        return reservaMapper.selectBooked(proveedorId, servicioId, desde, hasta, excluirId);
    }
}
