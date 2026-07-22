package bo.capital.tec.pet.notificacion.mapper;

import bo.capital.tec.pet.notificacion.entity.Notificacion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NotificacionMapper {
    Long insert(Notificacion notificacion);
    Notificacion selectById(@Param("id") Long id);
    List<Notificacion> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    List<Notificacion> selectByUsuarioId(@Param("usuarioId") Long usuarioId, @Param("offset") int offset, @Param("limit") int limit);
    long countByUsuarioIdAndLeida(@Param("usuarioId") Long usuarioId, @Param("leida") Boolean leida);
    void updateLeida(@Param("id") Long id);
    void deleteOld();
}
