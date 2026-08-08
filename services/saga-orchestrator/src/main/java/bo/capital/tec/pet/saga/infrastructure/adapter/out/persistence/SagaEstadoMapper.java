package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface SagaEstadoMapper {

    Optional<SagaEstadoRecord> findByReservaId(@Param("reservaId") Long reservaId);

    int insert(SagaEstadoRecord estado);

    int updateEstado(@Param("reservaId") Long reservaId,
                     @Param("estado") String estado,
                     @Param("pasoActual") String pasoActual,
                     @Param("motivo") String motivo,
                     @Param("eventoId") String eventoId);
}
