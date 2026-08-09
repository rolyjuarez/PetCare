package bo.capital.tec.pet.modules.soporte.mapper;

import bo.capital.tec.pet.modules.soporte.entity.Servicio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServicioMapper {

    Servicio selectById(@Param("id") Long id);

    List<Servicio> selectActive();
}
