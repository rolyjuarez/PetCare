package bo.capital.tec.pet.modules.mascota.domain.port;

import bo.capital.tec.pet.modules.mascota.domain.model.Especie;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface EspecieRepository {
Long insert(Especie especie);
    Especie selectById(@Param("id") Long id);
    List<Especie> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Especie especie);
    void softDelete(@Param("id") Long id);
}
