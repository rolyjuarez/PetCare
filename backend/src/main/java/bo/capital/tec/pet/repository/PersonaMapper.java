package bo.capital.tec.pet.repository;

import bo.capital.tec.pet.domain.Persona;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PersonaMapper {
    Long insert(Persona persona);
    Persona selectById(@Param("id") Long id);
    List<Persona> selectAll(@Param("nombre") String nombre, @Param("ci") String ci, @Param("email") String email, @Param("offset") int offset, @Param("limit") int limit);
    long countAll(@Param("nombre") String nombre, @Param("ci") String ci, @Param("email") String email);
    void update(Persona persona);
    void softDelete(@Param("id") Long id);
    Persona selectByCi(@Param("ci") String ci);
    Persona selectByEmail(@Param("email") String email);
}
