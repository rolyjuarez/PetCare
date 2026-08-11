package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.Vacuna;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface VacunaMapper {
    Long insert(Vacuna vacuna);
    Vacuna selectById(@Param("id") Long id);
    List<Vacuna> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    long countAll();
    void update(Vacuna vacuna);
    void softDelete(@Param("id") Long id);
}
