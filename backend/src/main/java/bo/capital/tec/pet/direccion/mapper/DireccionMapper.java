package bo.capital.tec.pet.direccion.mapper;

import bo.capital.tec.pet.direccion.entity.Direccion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface DireccionMapper {
    Long insert(Direccion direccion);
    Direccion selectById(@Param("id") Long id);
    List<Direccion> selectByCiudadId(@Param("ciudadId") Long ciudadId);
    void update(Direccion direccion);
    void softDelete(@Param("id") Long id);
}
