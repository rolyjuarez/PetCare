package bo.capital.tec.pet.common.kafka;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IdempotencyMapper {

    int insertEventoProcesado(EventoProcesado evento);

    Long existsEventoProcesado(@Param("eventId") String eventId);
}
