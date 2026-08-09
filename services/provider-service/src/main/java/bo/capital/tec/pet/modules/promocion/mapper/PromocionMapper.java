package bo.capital.tec.pet.modules.promocion.mapper;

import bo.capital.tec.pet.modules.promocion.entity.Promocion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PromocionMapper {

    Promocion selectById(@Param("id") Long id);

    Promocion selectByCodigo(@Param("codigo") String codigo);

    List<Promocion> selectByProveedorId(@Param("proveedorId") Long proveedorId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    long countByProveedorId(@Param("proveedorId") Long proveedorId);

    List<Promocion> selectActivasByProveedorId(@Param("proveedorId") Long proveedorId);

    boolean existeServicioDelProveedor(@Param("proveedorId") Long proveedorId,
                                       @Param("servicioId") Long servicioId);

    Long insert(Promocion promocion);

    void update(Promocion promocion);

    void softDelete(@Param("id") Long id);
}
