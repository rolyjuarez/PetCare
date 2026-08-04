package bo.capital.tec.pet.modules.notificacion.mapper;

import bo.capital.tec.pet.modules.notificacion.entity.Notificacion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NotificacionMapper {

    Long insert(Notificacion notificacion);
}
