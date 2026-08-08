package bo.capital.tec.pet.saga.infrastructure.adapter.out.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EventoProcesadoMapper {

    Long exists(@Param("eventId") String eventId);

    int insert(EventoProcesadoRecord evento);
}
