package bo.capital.tec.pet.modules.promocion.domain.port;

import bo.capital.tec.pet.modules.promocion.domain.model.Promocion;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface PromocionRepository {
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
