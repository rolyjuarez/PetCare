package bo.capital.tec.pet.servicio.mapper;

import bo.capital.tec.pet.servicio.entity.Servicio;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ServicioMapper {
    Long insert(Servicio servicio);
    Servicio selectById(@Param("id") Long id);
    List<Servicio> selectAll(@Param("nombre") String nombre, @Param("categoria") String categoria, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("categoria") String categoria);
    void update(Servicio servicio);
    void softDelete(@Param("id") Long id);
    List<Servicio> selectActive();
}
