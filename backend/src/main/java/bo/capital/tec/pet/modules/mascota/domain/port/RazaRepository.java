package bo.capital.tec.pet.modules.mascota.domain.port;

import bo.capital.tec.pet.modules.mascota.domain.model.Raza;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface RazaRepository {
Long insert(Raza raza);
    Raza selectById(@Param("id") Long id);
    List<Raza> selectByEspecieId(@Param("especieId") Long especieId);
    List<Raza> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Raza raza);
    void softDelete(@Param("id") Long id);
}
