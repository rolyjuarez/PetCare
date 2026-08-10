package bo.capital.tec.pet.modules.reserva.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.modules.reserva.domain.model.EstadoReserva;
import bo.capital.tec.pet.modules.reserva.domain.port.out.EstadoReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * Implementación MyBatis del puerto de repositorio de estados de reserva.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisEstadoReservaRepository implements EstadoReservaRepository {

    private final EstadoReservaMapper estadoReservaMapper;

    @Override
    public EstadoReserva findById(Long id) {
        return estadoReservaMapper.selectById(id);
    }

    @Override
    public EstadoReserva findByNombre(String nombre) {
        return estadoReservaMapper.selectByNombre(nombre);
    }
}
