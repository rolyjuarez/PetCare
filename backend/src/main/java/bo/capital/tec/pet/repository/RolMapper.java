package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.Rol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RolMapper {
    Long insert(Rol rol);
    Rol selectById(@Param("id") Long id);
    List<Rol> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Rol rol);
    void softDelete(@Param("id") Long id);
    Rol selectByNombre(@Param("nombre") String nombre);
    List<Rol> findByUsuarioId(@Param("usuarioId") Long usuarioId);
}
