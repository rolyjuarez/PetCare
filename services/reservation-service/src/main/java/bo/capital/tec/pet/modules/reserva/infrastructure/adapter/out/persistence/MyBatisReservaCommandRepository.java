package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.modules.reserva.domain.model.Reserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.ReservaCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementación MyBatis del puerto de repositorio del lado escritura.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisReservaCommandRepository implements ReservaCommandRepository {

    private final ReservaMapper reservaMapper;

    @Override
    public Reserva insert(Reserva reserva) {
        reservaMapper.insert(reserva);
        return reserva;
    }

    @Override
    public void update(Reserva reserva) {
        reservaMapper.update(reserva);
    }

    @Override
    public void updateEstado(Long id, Long estadoReservaId) {
        reservaMapper.updateEstado(id, estadoReservaId);
    }

    @Override
    public void updateRespuesta(Long id, Long proveedorId, Long estadoReservaId, String motivoRechazo) {
        reservaMapper.updateRespuesta(id, proveedorId, estadoReservaId, motivoRechazo);
    }

    @Override
    public void softDelete(Long id) {
        reservaMapper.softDelete(id);
    }

    @Override
    public Reserva findById(Long id) {
        return reservaMapper.selectById(id);
    }

    @Override
    public List<Reserva> findActivasPorCliente(Long clienteId) {
        return reservaMapper.selectByClienteId(clienteId, 0, 100);
    }

    @Override
    public List<Reserva> findBooked(Long proveedorId, Long servicioId,
                                    LocalDate desde, LocalDate hasta, Long excluirId) {
        return reservaMapper.selectBooked(proveedorId, servicioId, desde, hasta, excluirId);
    }
}
