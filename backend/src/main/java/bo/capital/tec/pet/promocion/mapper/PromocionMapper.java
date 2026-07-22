package bo.capital.tec.pet.promocion.mapper;

import bo.capital.tec.pet.promocion.entity.Promocion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PromocionMapper {
    Long insert(Promocion promocion);
    Promocion selectById(@Param("id") Long id);
    Promocion selectByCodigo(@Param("codigo") String codigo);
    List<Promocion> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    List<Promocion> selectActive(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    long countActive();
    void update(Promocion promocion);
    void softDelete(@Param("id") Long id);
    void incrementUsos(@Param("id") Long id);
}
