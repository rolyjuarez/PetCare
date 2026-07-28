package bo.capital.tec.pet.modules.mascota.mapper;

import bo.capital.tec.pet.modules.mascota.entity.Especie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EspecieMapper {
    Long insert(Especie especie);
    Especie selectById(@Param("id") Long id);
    List<Especie> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Especie especie);
    void softDelete(@Param("id") Long id);
}
