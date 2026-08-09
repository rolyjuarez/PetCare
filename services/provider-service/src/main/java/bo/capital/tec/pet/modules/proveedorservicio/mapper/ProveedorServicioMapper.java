package bo.capital.tec.pet.modules.proveedorservicio.mapper;

import bo.capital.tec.pet.modules.proveedorservicio.entity.ProveedorServicio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProveedorServicioMapper {
    Long insert(ProveedorServicio servicio);
    ProveedorServicio selectById(@Param("id") Long id);
    List<ProveedorServicio> selectByProveedorId(@Param("proveedorId") Long proveedorId);
    List<ProveedorServicio> selectActivosByProveedorId(@Param("proveedorId") Long proveedorId);
    List<ProveedorServicio> selectActivosByCategoria(@Param("categoria") String categoria);
    void update(ProveedorServicio servicio);
    void updateActivo(@Param("id") Long id, @Param("activo") Boolean activo);
    void softDelete(@Param("id") Long id);
}
