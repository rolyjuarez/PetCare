package bo.capital.tec.pet.vacuna.mapper;

import bo.capital.tec.pet.vacuna.entity.RegistroVacunacion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RegistroVacunacionMapper {
    Long insert(RegistroVacunacion rv);
    RegistroVacunacion selectById(@Param("id") Long id);
    List<RegistroVacunacion> selectByMascotaId(@Param("mascotaId") Long mascotaId, @Param("offset") int offset, @Param("limit") int limit);
    long countByMascotaId(@Param("mascotaId") Long mascotaId);
    List<RegistroVacunacion> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(RegistroVacunacion rv);
    void softDelete(@Param("id") Long id);
}
