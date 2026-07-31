package bo.capital.tec.pet.modules.vacuna.domain.port;

import bo.capital.tec.pet.modules.vacuna.domain.model.Vacuna;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface VacunaRepository {
Long insert(Vacuna vacuna);
    Vacuna selectById(@Param("id") Long id);
    List<Vacuna> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Vacuna vacuna);
    void softDelete(@Param("id") Long id);
}
