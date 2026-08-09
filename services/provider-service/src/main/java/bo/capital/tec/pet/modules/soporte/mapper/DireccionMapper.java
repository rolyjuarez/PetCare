package bo.capital.tec.pet.modules.soporte.mapper;

import bo.capital.tec.pet.modules.soporte.entity.Direccion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DireccionMapper {

    Long insert(Direccion direccion);

    Direccion selectById(@Param("id") Long id);

    void update(Direccion direccion);
}
