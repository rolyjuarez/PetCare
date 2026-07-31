package bo.capital.tec.pet.modules.menu.domain.port;

import bo.capital.tec.pet.modules.menu.domain.model.Permiso;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PermisoRepository {
Long insert(Permiso permiso);
    Permiso selectById(@Param("id") Long id);
    List<Permiso> selectByRolId(@Param("rolId") Long rolId);
    List<Permiso> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Permiso permiso);
    void softDelete(@Param("id") Long id);
}
