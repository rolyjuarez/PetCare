package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import bo.capital.tec.pet.saga.domain.model.EstadoSaga;
import bo.capital.tec.pet.saga.domain.model.SagaEstado;
import bo.capital.tec.pet.saga.domain.port.out.SagaEstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto de persistencia de la saga usando
 * MyBatis sobre la tabla {@code saga_estado}.
 */
@Repository
@RequiredArgsConstructor
public class MyBatisSagaEstadoRepository implements SagaEstadoRepository {

    private final SagaEstadoMapper mapper;

    @Override
    public Optional<SagaEstado> findByReservaId(Long reservaId) {
        return mapper.findByReservaId(reservaId).map(this::toDomain);
    }

    @Override
    public void insert(SagaEstado estado) {
        mapper.insert(toRecord(estado));
    }

    @Override
    public void updateEstado(Long reservaId, EstadoSaga estado, String pasoActual,
                             String motivo, String eventoId) {
        mapper.updateEstado(reservaId, estado.name(), pasoActual, motivo, eventoId);
    }

    private SagaEstadoRecord toRecord(SagaEstado estado) {
        SagaEstadoRecord record = new SagaEstadoRecord();
        record.setReservaId(estado.getReservaId());
        record.setCodigoReserva(estado.getCodigoReserva());
        record.setEstado(estado.getEstado().name());
        record.setPasoActual(estado.getPasoActual());
        record.setMotivo(estado.getMotivo());
        record.setUltimoEventoId(estado.getUltimoEventoId());
        record.setCreadaEn(estado.getCreadaEn());
        record.setActualizadaEn(estado.getActualizadaEn());
        return record;
    }

    private SagaEstado toDomain(SagaEstadoRecord record) {
        SagaEstado estado = new SagaEstado();
        estado.setId(record.getId());
        estado.setReservaId(record.getReservaId());
        estado.setCodigoReserva(record.getCodigoReserva());
        estado.setEstado(EstadoSaga.valueOf(record.getEstado()));
        estado.setPasoActual(record.getPasoActual());
        estado.setMotivo(record.getMotivo());
        estado.setUltimoEventoId(record.getUltimoEventoId());
        estado.setCreadaEn(record.getCreadaEn());
        estado.setActualizadaEn(record.getActualizadaEn());
        return estado;
    }
}
