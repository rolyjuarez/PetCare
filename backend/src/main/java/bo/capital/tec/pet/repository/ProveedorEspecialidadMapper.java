package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.ProveedorEspecialidad;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProveedorEspecialidadMapper {
    Long insert(ProveedorEspecialidad pe);
    List<ProveedorEspecialidad> selectByProveedorId(@Param("proveedorId") Long proveedorId);
    void deleteByProveedorId(@Param("proveedorId") Long proveedorId);
}
