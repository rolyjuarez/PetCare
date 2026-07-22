package bo.capital.tec.pet.mascota.mapper;

import bo.capital.tec.pet.mascota.entity.Raza;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RazaMapper {
    Long insert(Raza raza);
    Raza selectById(@Param("id") Long id);
    List<Raza> selectByEspecieId(@Param("especieId") Long especieId);
    List<Raza> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Raza raza);
    void softDelete(@Param("id") Long id);
}
