package bo.capital.tec.pet.modules.bitacora.domain.port;

import bo.capital.tec.pet.modules.bitacora.domain.model.Bitacora;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BitacoraRepository {
void insert(Bitacora bitacora);
    Bitacora selectById(@Param("id") Long id);
    List<Bitacora> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    List<Bitacora> selectByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("offset") int offset, @Param("limit") int limit);
    List<Bitacora> selectByEntidad(@Param("entidad") String entidad, @Param("offset") int offset, @Param("limit") int limit);
    List<Bitacora> selectByFilters(@Param("usuarioId") Long usuarioId, @Param("entidad") String entidad, @Param("accion") String accion, @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin, @Param("offset") int offset, @Param("limit") int limit);
    long countByFilters(@Param("usuarioId") Long usuarioId, @Param("entidad") String entidad, @Param("accion") String accion, @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin);
}
