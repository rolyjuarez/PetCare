package bo.capital.tec.pet.modules.soporte.mapper;

import bo.capital.tec.pet.modules.soporte.entity.Persona;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonaMapper {

    Long insert(Persona persona);

    Persona selectById(@Param("id") Long id);

    Persona selectByCi(@Param("ci") String ci);

    Persona selectByEmail(@Param("email") String email);

    void update(Persona persona);

    List<String> selectClienteEmails();
}
